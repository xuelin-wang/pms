(ns user
  (:require
    [pms.game.schema :as sc]
    [pms.game.db :as db]
    [com.walmartlabs.lacinia :as lacinia]
    [com.walmartlabs.lacinia.pedestal :as lp]
    [io.pedestal.http :as http]
    [clojure.java.browse :refer [browse-url]]
    [integrant.core :as ig]
    )
  )

(comment
  (defonce server nil)

  (defn start-server
    [_]
    (let [server (-> schema
                     (lp/service-map {:graphiql true})
                     http/create-server
                     http/start)]
      ;  (browse-url "http://localhost:8888/")
      server))

  (defn stop-server
    [server]
    (http/stop server)
    nil)

  (defn start
    []
    (alter-var-root #'server start-server)
    :started)

  (defn stop
    []
    (alter-var-root #'server stop-server)
    :stopped)
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

(def system (ig/init sysconf))

(defn q
  [query-string]
  (let [schema (:game/cgg-schema system)]
    (-> (lacinia/execute schema query-string nil nil)
        sc/simplify)
    )
  )

