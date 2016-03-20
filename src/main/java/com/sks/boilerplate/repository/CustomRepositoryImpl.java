package com.sks.boilerplate.repository;

import static org.springframework.util.StringUtils.hasText;

import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.sks.boilerplate.annotations.Filterable;
import com.sks.boilerplate.entity.BaseEntity;
import com.sks.boilerplate.enums.ErrorKeys;
import com.sks.boilerplate.exception.ApplicationException;
import com.sks.boilerplate.repository.util.PredicateBuilder;

import ch.qos.logback.classic.Logger;
import lombok.Setter;

public class CustomRepositoryImpl<T extends BaseEntity<ID>, ID extends Serializable> extends SimpleJpaRepository<T, ID>
		implements CustomRepository<T, ID> {

	private final Class<T> classType;

	@Setter
	private PredicateBuilder<T> predicateBuilder;

	public CustomRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager em) {
		super(entityInformation, em);
		this.classType = entityInformation.getJavaType();
		this.entityManager = em;
		this.entityInformation = entityInformation;
	}

	private final EntityManager entityManager;
	private List<Field> filterableFields;
	private final Logger logger = (Logger) LoggerFactory.getLogger(this.getClass());

	private final JpaEntityInformation<T, ID> entityInformation;

	/**
	 * @param t
	 *            object by which the bean has to be filtered
	 * @return the {@link Iterable} T that matches the input t
	 */
	@Override
	public List<T> filter(T t) {
		final CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		final CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(this.classType);
		final Root<T> root = criteriaQuery.from(this.classType);

		final List<Predicate> conditions = this.getPredicatesForClass(t, criteriaBuilder, root);
		if (null != this.predicateBuilder) {
			conditions.addAll(this.predicateBuilder.create(t, criteriaBuilder, root));
		}
		this.logger.info("Running query with " + conditions.size() + " predicates for the class " + this.classType);

		// With the list of conditions, come up with the final query
		criteriaQuery.where(conditions.toArray(new Predicate[] {}));
		if (!t.isAsc()) {
			criteriaQuery.orderBy(criteriaBuilder.desc(root.get("id")));
		}

		// Execute the query and return the result
		TypedQuery<T> createQuery = this.entityManager.createQuery(criteriaQuery);
		// If the pageCount is specified , then we have to get paginate the
		// records
		if (null != t.getPage()) {
			createQuery.setFirstResult(t.getOffset());
			createQuery.setMaxResults(t.getPageSize());
		}
		return createQuery.getResultList();
	}

	/**
	 * @param t
	 *            entity based on which filter is to created
	 * @param criteriaBuilder
	 *            {@link CriteriaBuilder} object
	 * @param criteriaQuery
	 *            {@link CriteriaQuery} Query to be used
	 * @return List<Predicate> the list of predicates based on the criteria
	 *         builder
	 */
	private List<Predicate> getPredicatesForClass(T t, CriteriaBuilder criteriaBuilder, Root<T> root) {

		// Getting the property to be accessed
		final List<Field> fields = this.getAllFilterableFields(t);
		// List to be returned
		final List<Predicate> returnList = new ArrayList<Predicate>();

		AccessibleObject.setAccessible(fields.toArray(new Field[] {}), true);

		Predicate condition;
		Expression<String> expression;
		try {
			for (final Field field : fields) {
				Object value;
				try {
					value = PropertyUtils.getSimpleProperty(t, field.getName());
				} catch (final NoSuchMethodException noSuchMethodException) {
					this.logger.warn("Obtained " + noSuchMethodException.getMessage() + " when trying to get value for "
							+ field.getName() + " . Ignoring the property");
					continue;
				}

				// If the value is null or the field is not filter-able, do not
				// create the condition
				if (isFilterable(field) && null != value && hasText(value.toString())) {
					// Add the condition based no the field name and the value
					expression = root.get(field.getName());
					this.logger.trace("Creating the string check for the field " + field.getName()
							+ " with the value : " + value);
					if (field.getType() == String.class) {
						expression = criteriaBuilder.lower(expression);
						// create the condition
						condition = criteriaBuilder.equal(expression, value.toString().toLowerCase());
					} else {

						condition = criteriaBuilder.equal(expression, value);
					}
					// Add the condition to the list
					returnList.add(condition);
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
			// This condition should never occur ideally
			throw new ApplicationException(ErrorKeys.INTERNAL_SERVER_ERROR, e);
		}

		return returnList;
	}

	/**
	 *
	 * @param t
	 *            entity who has to be scanned
	 * @return the list of fields that can be filtered
	 */
	private List<Field> getAllFilterableFields(T t) {
		if (this.filterableFields != null) {
			return this.filterableFields;
		}
		this.filterableFields = new ArrayList<Field>();
		if (t instanceof BaseEntity) {
			this.filterableFields.addAll(Arrays.asList(BaseEntity.class.getDeclaredFields()));
		}

		this.filterableFields.addAll(Arrays.asList(this.classType.getDeclaredFields()));

		final Iterator<Field> fieldIterator = this.filterableFields.iterator();
		Field field;
		while (fieldIterator.hasNext()) {
			field = fieldIterator.next();
			if (!isAcceptableFieldModifiers(field.getModifiers())) {
				fieldIterator.remove();
			}
		}
		this.logger.info("Obtained " + this.filterableFields.size() + "  fields to be fileted on for the class "
				+ this.classType);
		return this.filterableFields;
	}

	/**
	 * @param field
	 *            {@link Field} that has to be checked
	 * @return boolean if the field is annotated with {@link Filterable}
	 *         annotation
	 */
	private static boolean isFilterable(Field field) {
		return field.getAnnotation(Filterable.class) != null;
	}

	/**
	 * @param fieldModifier
	 *            int value stating the field modifier
	 * @return true if neither static nor final
	 */
	private static boolean isAcceptableFieldModifiers(int fieldModifier) {
		// These fields are final or static is not owned by the object (might be
		// owned by the CLASS)
		return !(Modifier.isStatic(fieldModifier) || Modifier.isFinal(fieldModifier));
	}

	@Override
	@Transactional
	@Modifying(clearAutomatically = true)
	public void updateField(ID id, String field, Object newValue) {
		this.logger.info("Updating the field " + field + " for the entity " + this.classType + "[" + id + "] to value "
				+ newValue);
		StringBuilder builder = new StringBuilder("update %s x set %s='%s' where id=%s");

		this.entityManager
				.createQuery(
						String.format(builder.toString(), this.entityInformation.getEntityName(), field, newValue, id))
				.executeUpdate();
		this.entityManager.detach(this.findOne(id));
	}
}
