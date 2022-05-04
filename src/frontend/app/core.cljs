(ns app.core
  (:require
   [app.app :refer [app]]
   [app.ui.main :refer [Main]]
   [helix.core :as hx :refer [$]]
   [keechma.next.core :as keechma]
   [keechma.next.helix.core :refer [KeechmaRoot]]
   [react :as react]
   [react-dom :as rdom]))

(defonce app-instance* (atom nil))

(defn init
  []
  (when-let [app-instance @app-instance*] (keechma/stop! app-instance))
  (let [app-instance (keechma/start! app)]
    (reset! app-instance* app-instance)
    (rdom/render ($ react/StrictMode
                   ($ KeechmaRoot {:keechma/app app-instance} ($ Main)))
                 (js/document.getElementById "app"))))

(defn ^:dev/after-load reload
  "Render the toplevel component for this app."
  []
  (rdom/unmountComponentAtNode (js/document.getElementById "app"))
  (init))

(defn ^:export main "Run application startup logic." [] (init))
