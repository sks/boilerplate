package importjson

type Imports map[string]string

type ImportmapOutput struct {
	Imports Imports `json:"imports"`
}

func DefaultMap() Imports {
	return Imports{
		"@polyglot-mf/root-config":       "https://polyglot.microfrontends.app/root-config/1341a566ef5b52e92bd8d98c4698a47fc9f22b54/polyglot-mf-root-config.js",
		"single-spa":                     "https://polyglot.microfrontends.app/npm/single-spa@5.9.0/lib/system/single-spa.min.js",
		"react":                          "https://cdn.jsdelivr.net/npm/react@17.0.1/umd/react.production.min.js",
		"react-dom":                      "https://polyglot.microfrontends.app/npm/react-dom@17.0.1/umd/react-dom.production.min.js",
		"@polyglot-mf/navbar":            "https://polyglot.microfrontends.app/navbar/c51a2e455fbec2d96b6af3b120a922b09f8e74fe/polyglot-mf-navbar.js",
		"@polyglot-mf/styleguide":        "https://polyglot.microfrontends.app/styleguide/735a89a8981b97cc435e3fb0b601379dd3f252f9/polyglot-mf-styleguide.js",
		"vue":                            "https://polyglot.microfrontends.app/npm/vue@2.6.12/dist/vue.min.js",
		"vue-router":                     "https://polyglot.microfrontends.app/npm/vue-router@3.4.9/dist/vue-router.min.js",
		"@polyglot-mf/clients":           "https://polyglot.microfrontends.app/clients/ac459b109ef8bcbc75777279223347ff7b3b55e9/js/app.js",
		"@polyglot-mf/account-settings":  "https://polyglot.microfrontends.app/account-settings/ebc4331a60d87cecba4a95c5c35d25e3eeb3f4df/polyglot-mf-account-settings.js",
		"single-spa-angularjs":           "https://polyglot.microfrontends.app/npm/single-spa-angularjs@4.1.0/lib/single-spa-angularjs.js",
		"@polyglot-mf/navbar/":           "https://polyglot.microfrontends.app/navbar/c51a2e455fbec2d96b6af3b120a922b09f8e74fe/",
		"@polyglot-mf/root-config/":      "https://polyglot.microfrontends.app/root-config/1341a566ef5b52e92bd8d98c4698a47fc9f22b54/",
		"@polyglot-mf/styleguide/":       "https://polyglot.microfrontends.app/styleguide/735a89a8981b97cc435e3fb0b601379dd3f252f9/",
		"@polyglot-mf/clients/":          "https://polyglot.microfrontends.app/clients/ac459b109ef8bcbc75777279223347ff7b3b55e9/js/",
		"@polyglot-mf/account-settings/": "https://polyglot.microfrontends.app/account-settings/ebc4331a60d87cecba4a95c5c35d25e3eeb3f4df/",
	}
}
