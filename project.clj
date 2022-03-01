(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :min-lein-version "2.0.0"
  :dependencies [[com.flexiana/framework "0.3.4" :exclusions [funcool/cuerdas]]
                 [com.verybigthings/commons "92adaec7f29f35178f050276e51873676de668f2"]
                 [com.verybigthings/funicular "fb4cb3ae49a9246f4489396c047779b73e9c82ba"]
                 [com.verybigthings/penkala "8f38814dcfe5a23ee2c6fcdd5d2c48ccd6f4f1c7"]
                 [com.verybigthings/pgerrors "b7a95d13cee17ec9a0bcbab3a5107950f7113ea9"]
                 [lambdaisland/regal "0.0.143"]
                 [medley/medley "1.3.0"]
                 [metosin/jsonista "0.3.5"]
                 [metosin/malli "0.8.3"]
                 [metosin/muuntaja "0.6.8"]
                 [metosin/reitit "0.5.16"]
                 [migratus/migratus "1.3.6"]
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "0.7.559"]
                 [org.clojure/core.match "1.0.0"]
                 [org.clojure/spec.alpha "0.2.187"]
                 [org.clojure/tools.namespace "1.1.0"]]
  :git-down {keechma.next/toolbox        {:coordinates keechma/keechma-next-toolbox}
             clj-kondo/config            {:coordinates clj-kondo/config}
             com.verybigthings/funicular {:coordinates VeryBigThings/funicular}
             com.verybigthings/commons   {:coordinates VeryBigThings/clojure-commons}
             com.verybigthings/pgerrors  {:coordinates VeryBigThings/pgerrors}
             com.verybigthings/penkala   {:coordinates retro/penkala}}
  :plugins [[lein-shadow "0.4.0"]
            [com.github.clj-kondo/lein-clj-kondo "0.1.3"]
            [reifyhealth/lein-git-down "0.4.1"]]
  :repositories [["public-github" {:url "git://github.com"}]
                 ["private-github" {:url "git://github.com" :protocol :ssh}]]
  :main ^:skip-aot app.core
  :uberjar-name "app.jar"
  :middleware [lein-git-down.plugin/inject-properties]
  :source-paths ["src/backend" "src/frontend" "src/shared"]
  :clean-targets ^{:protect false} ["resources/public/assets/js" "target"]
  :profiles {:uberjar   {:aot      :all
                         :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}
             :frontend  {:dependencies [[applied-science/js-interop "0.3.3"]
                                        [com.cognitect/transit-cljs "0.8.269"]
                                        [hodgepodge/hodgepodge "0.1.3"]
                                        [keechma.next/toolbox "bdeebce5f1b296fc971035b49f859dfaaa28883b"]
                                        [lambdaisland/fetch "1.0.41"]
                                        [lilactown/helix "0.1.5"]
                                        [org.clojure/clojurescript "1.11.4"]
                                        [thheller/shadow-cljs "2.17.5"]]}
             :clj-kondo {:dependencies [[clj-kondo/clj-kondo "2022.02.09"]
                                        [clj-kondo/config "e2e156c53c6c228fee7242629b41013f3e55051d"]]}
             :dev       {:main         user
                         :source-paths ["dev" "config/dev"]
                         :dependencies [[binaryage/devtools "1.0.3"]
                                        [hawk "0.2.11"]
                                        [nrepl/nrepl "0.8.3"]
                                        [vlaaad/reveal "1.3.270"]]}
             :local     {:source-paths ["config/local"]}
             :prod      {:source-paths ["config/prod"]}
             :test      {:source-paths ["config/test"]
                         :dependencies [[io.zonky.test.postgres/embedded-postgres-binaries-darwin-amd64 "14.1.0"]
                                        [io.zonky.test.postgres/embedded-postgres-binaries-linux-amd64 "14.1.0"]
                                        [io.zonky.test/embedded-postgres "1.3.1"]
                                        [lambdaisland/kaocha "1.63.998"]
                                        [nubank/state-flow "5.13.1"]
                                        [org.clojure/test.check "0.10.0"]]}}
  :shadow-cljs {:nrepl  {:port 8777}
                :builds {:app {:target     :browser
                               :output-dir "resources/public/assets/js"
                               :asset-path "assets/js"
                               :modules    {:app {:init-fn  app.core/init
                                                  :preloads [devtools.preload]}}}}}
  :aliases {"test"             ["with-profile" "test" "run" "-m" "kaocha.runner"]
            "lint"             ["clj-kondo" "--lint" "src" "test" "dev"]
            "release-backend"  ["with-profile" "prod" "do"
                                "clean," "uberjar"]
            "release-frontend" ["with-profile" "frontend" "do"
                                "clean," ["shadow" "release" "app"]]})
