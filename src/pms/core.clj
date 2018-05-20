(ns pms.core
  (:require
    [pms.game.schema :as sc]
    [pms.game.db :as db]
    [com.walmartlabs.lacinia :as lacinia]
    [com.walmartlabs.lacinia.pedestal :as lp]
    [io.pedestal.http :as http]
    [integrant.core :as ig]
    )
  (:gen-class)
  )

(def sysconf
  {
   :server/jetty {:schema (ig/ref :game/cgg-schema)}
   :game/cgg-schema {:db (ig/ref :game/db)}
   :game/db {}
   })

(defmethod ig/init-key :game/db [_ _]
  (db/new-db)
  )

(defmethod ig/halt-key! :game/db [_ _]
  nil
  )

(defmethod ig/init-key :game/cgg-schema [_ {:keys [db]}]
  (sc/load-schema db)
  )

(defmethod ig/halt-key! :game/cgg-schema [_ _]
  nil
  )

(defmethod ig/init-key :server/jetty [_ {:keys [schema]}]
  (let [server (-> schema
                   (lp/service-map {:graphiql true})
                   http/create-server
                   http/start)]
    ;  (browse-url "http://localhost:8888/")
    server)
  )

(defmethod ig/halt-key! :server/jetty [_ server]
  (http/stop server)
  nil
  )

(comment
  (defn q
    [query-string]
    (let [schema (:game/cgg-schema system)]
      (-> (lacinia/execute schema query-string nil nil)
          sc/simplify)
      )
    )
  )

(defn -main [& args]
  (ig/init sysconf))
