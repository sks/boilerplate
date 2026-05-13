# IaC Registry Mapping Report

## Summary

- **Total Groups**: 1,121 (conn-aws-001 → conn-aws-1121)
- **Total Managed Resources**: 12,726
- **Max Resources per Group**: 80
- **Orphaned Resources**: 245 (modularized into 11 aios-* modules)
- **AWS Resource Types**: 77 classified and scaffolded with import blocks

## Structure

```
iac-split/
  groups/
    conn-aws-001/  # main.tf, providers.tf, imports.tf, metadata.json
    conn-aws-002/
    ...
    conn-aws-1121/
  orphans_bundle.json
  logical_group_manifest_enriched.json
  registry_mapping_report.md
```

## Notes

- All modules include `lifecycle { ignore_changes = all }` guards for safe initial import
- Remove guards before production apply after validation
- aws_key_pair resources require public key values to be supplied
- aws_elasticsearch_domain should be migrated to aws_opensearch_domain
