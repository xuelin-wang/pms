(ns xl.pms.server
  (:require
    [xl.pms.game.schema :as sc]
    [xl.pms.game.db :as db]
    [com.walmartlabs.lacinia :as lacinia]
    [com.walmartlabs.lacinia.pedestal :as lp]
    [io.pedestal.http :as http]
    [clojure.java.io :as io]
    [xl.pms.service.examples :as examples]
    [xl.pms.service.redis]
    [clj-yaml.core]
    [xl.db.redis]
    [integrant.core :as ig]
    [clojure.tools.logging :as log]
    )
  )

(defn get-conf [name]
  {
   :server/jetty {:app-conf (ig/ref :app/conf) :redis (ig/ref :db/redis)
                  :schema (ig/ref :game/cgg-schema)}
   :game/cgg-schema {:db (ig/ref :game/db)}
   :game/db {}
   :db/redis {:app-conf (ig/ref :app/conf)}
   :app/conf {:name name}
   }
  )

(defmethod ig/init-key :app/conf [_ {:keys [name]}]
  (-> (io/resource (str "conf/" name ".yml"))
      slurp
      clj-yaml.core/parse-string)
  )

(defmethod ig/init-key :db/redis [_ {:keys [app-conf]}]
  (let [redis-conf (:redis app-conf)
        {:keys [host port]} redis-conf
        ]
    (println (str "app-conf:\n" app-conf))
    (xl.db.redis/create-jedis-pool host (int port))
    )
  )

(defmethod ig/halt-key! :db/redis [_ pool]
  (try
    (.close pool)
    (catch Exception e (log/warn e "Failed to close jedis pool."))))

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

(defmethod ig/init-key :server/jetty [_ {:keys [app-conf redis schema]}]
  (let [
        hello-route ["/hello/:user-id" :get examples/hello-interceptor :route-name ::hello-user]
        redis-get-route ["/redis/:cmd" :get (xl.pms.service.redis/create-redis-interceptor redis)
                     :route-name ::redis-get]
        redis-post-route ["/redis/:cmd" :post (xl.pms.service.redis/create-redis-interceptor redis)
                     :route-name ::redis-post]
        {:keys [services]} app-conf
        service-map (-> schema
                        (lp/service-map services)
                        (update ::http/routes conj hello-route redis-get-route redis-post-route))
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
