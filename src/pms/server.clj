(ns pms.server
  (:require
    [pms.game.schema :as sc]
    [pms.game.db :as db]
    [com.walmartlabs.lacinia :as lacinia]
    [com.walmartlabs.lacinia.pedestal :as lp]
    [io.pedestal.http :as http]
    [integrant.core :as ig]
    )
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

(def hello-interceptor
  {
   :name ::hello-user
   :enter (fn [{:keys [request] :as context}]
            (let [{:keys [path-params]} request]
              (assoc context :response {:body (str "Hello, " (:user-id path-params)) :status 200})
              )
            )
   }
  )

(defmethod ig/init-key :server/jetty [_ {:keys [schema]}]
  (let [
        hello-route ["/hello/:user-id" :get hello-interceptor :route-name ::hello-user]
        service-map (-> schema
                        (lp/service-map {:graphiql true :port 8888})
                        (update ::http/routes conj hello-route))
        ;        _ (println service-map)
        server (-> service-map
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
