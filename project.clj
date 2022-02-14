(defproject app "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :min-lein-version "2.0.0"
  :dependencies [[binaryage/oops "0.7.1"]
                 [buddy/buddy-auth "2.2.0"]
                 [camel-snake-kebab/camel-snake-kebab "0.4.2"]
                 [com.cognitect/transit-clj "1.0.324"]
                 [digest/digest  "1.4.10"]
                 [hiccup/hiccup  "1.0.5"]
                 [http-kit/http-kit  "2.5.3"]
                 [lambdaisland/regal  "0.0.89"]
                 [medley/medley  "1.2.0"]
                 [metosin/jsonista "0.3.5"]
                 [metosin/malli  "0.5.1"]
                 [metosin/muuntaja  "0.6.7"]
                 [metosin/reitit  "0.5.11"]
                 [metosin/sieppari  "0.0.0-alpha13"]
                 [msolli/proletarian "1.0.32-alpha"] ;; this should be equiv to ee4155 commit sha
                 ;[nl.mediquest/duct.module.reitit "1.0.1"]
                 [org.clojure/core.async "0.7.559"]
                 [org.clojure/core.match "1.0.0"]
                 [org.clojure/spec.alpha "0.2.187"]
                 [org.postgresql/postgresql "42.2.18"]
                 [ring-cors/ring-cors "0.1.13"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-jetty-adapter "1.7.1"]
                 [ring/ring-ssl "0.3.0"]
                 [seancorfield/next.jdbc "1.1.613"]
                 [tick/tick "0.4.30-alpha"]
                 [thheller/shadow-cljs "2.14.4"]
                 ;; FIX
                 [funcool/cuerdas "2022.01.14-391"]
                 ;; FLEX
                 [clj-http/clj-http "3.12.1"]  ;; updated namespace
                 [com.flexiana/framework "0.3.4" :exclusions [funcool/cuerdas]]
                 [com.fzakaria/slf4j-timbre "0.3.21"]
                 [migratus/migratus "1.3.5"]   ;; updated namespace
                 [org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.namespace "1.1.0"]
                 [com.verybigthings/commons "1feaf1447c7d71472854fad9c1e70ae58c3223e3"]
                 [com.verybigthings/pgerrors "b7a95d13cee17ec9a0bcbab3a5107950f7113ea9"]
                 [com.verybigthings/funicular "fb4cb3ae49a9246f4489396c047779b73e9c82ba"]
                 [com.verybigthings/penkala "8f38814dcfe5a23ee2c6fcdd5d2c48ccd6f4f1c7"]]
                 ;;[re-frame "1.1.2"]
                 ;;[reagent "0.10.0"]
                 ;;[thheller/shadow-cljs "2.11.26"]

  :git-down {keechma.next/toolbox {:coordinates keechma/keechma-next-toolbox}
             keechma/malli-forms  {:coordinates keechma/keechma-malli-forms}
             clj-kondo/config {:coordinates clj-kondo/config}
             com.cognitect/test-runner {:coordinates com.cognitect/test-runner}
             com.verybigthings/funicular {:coordinates VeryBigThings/funicular}
             com.verybigthings/commons {:coordinates VeryBigThings/clojure-commons}
             com.verybigthings/pgerrors {:coordinates VeryBigThings/pgerrors}
             com.verybigthings/penkala {:coordinates retro/penkala}}
  :plugins [[lein-shadow "0.4.0"]
            [reifyhealth/lein-git-down "0.4.1"]]
  :repositories [["public-github" {:url "git://github.com"}]
                 ["private-github" {:url "git://github.com" :protocol :ssh}]]
  :main ^:skip-aot app.core
  :uberjar-name "app.jar"
  :middleware     [lein-git-down.plugin/inject-properties]
  :source-paths ["src/backend" "src/frontend" "src/shared" "src/utils"]
  :clean-targets ^{:protect false} ["resources/public/assets/js/compiled" "target"]
  :profiles {;; VBT profile
             :frontend {:dependencies [;; VBT
                                       [org.clojure/clojurescript "1.10.866"]
                                       [g7s/module.shadow-cljs "0.1.2"]
                                       [thheller/shadow-cljs "2.14.4"]
                                       [babashka/process "0.0.2"]
                                       [lambdaisland/fetch "0.0.23"]
                                       [applied-science/js-interop "0.2.7"]
                                       [com.cognitect/transit-cljs "0.8.264"]
                                       [hodgepodge/hodgepodge "0.1.3"]
                                       [binaryage/devtools "1.0.3"]
                                       [lilactown/helix "0.1.1"]
                                       [keechma.next/toolbox "0c605f8e36c51463e433f3130441bd663ae008e6"]]}
             ;; VBT profile
             :clj-kondo {:dependencies [[clj-kondo/clj-kondo  "2021.06.01"]
                                        [nubank/state-flow  "5.13.1"]
                                        [clj-kondo/config    "e2e156c53c6c228fee7242629b41013f3e55051d"]]}
             ;; VBT profile 
             :runner {:dependencies [[com.cognitect/test-runner "f7ef16dc3b8332b0d77bc0274578ad5270fbfedd"]]}
             :kaocha {:dependencies [[lambdaisland/kaocha "1.62.993"]]}
             :dev   {:source-paths ["dev" "config/dev"]
                     :dependencies   [;; VBT
                                      [commons-io/commons-io "2.6"]
                                      [nrepl/nrepl "0.8.3"]
                                      ;; FLEX
                                      [vlaaad/reveal "1.3.264"]
                                      [binaryage/devtools "1.0.3"]]}
             :local {:source-paths ["config/local"]}
             :prod  {:source-paths ["config/prod"]}
             :test  {:source-paths ["config/test" "src/utils"]
                     :dependencies   [;; VBT
                                      [org.clojure/test.check "0.10.0"]
                                      [nubank/state-flow "5.13.1"]
                                      [io.zonky.test/embedded-postgres "1.3.1"]
                                      [io.zonky.test.postgres/embedded-postgres-binaries-darwin-amd64 "14.1.0"]
                                      [io.zonky.test.postgres/embedded-postgres-binaries-linux-amd64 "14.1.0"]
                                      [lambdaisland/kaocha "1.60.977"]
                                      [ring/ring-mock "0.4.0"]
                                      ;; FLEX
                                      [vlaaad/reveal "1.3.264"]
                                      [kerodon "0.9.1"]]}}
  :shadow-cljs {:nrepl  {:port 8777}
                :builds {:app {:target     :browser
                               :output-dir "resources/public/assets/js/compiled"
                               :asset-path "assets/js/compiled"
                               :modules    {:app {:init-fn app.core/init
                                                  :preloads [devtools.preload]}}}}}
  :aliases {"ci"      ["do" "clean," "cloverage," "lint," "uberjar"]
            "clj-kondo" ["run" "-m" "clj-kondo.main"] ;; VBT
            "runner" ["run" "-m" "cognitect.test-runner"
                      "run" "-d" "test"]
            "kaocha" ["with-profile" "+kaocha,+test" "run" "-m" "kaocha.runner"]
            "kondo"   ["run" "-m" "clj-kondo.main" "--lint" "src" "test"]
            "lint"    ["do" "kondo," "eastwood," "kibit"]
            "migrate" ["run" "-m" "framework.db.main"]
            "seed"    ["run" "-m" "framework.db.seed"]
            "watch"   ["with-profile" "frontend,dev" "do"
                       ["shadow" "watch" "app" "browser-test" "karma-test"]]  ;; added frontend profile to run alongside dev
            "release" ["with-profile" "prod" "do"
                       ["shadow" "release" "app"]]})
