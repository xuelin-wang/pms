(ns user
  (:require
    [pms.server]
    [pms.game.schema :as sc]
    [com.walmartlabs.lacinia :as lacinia]
    [integrant.core :as ig]
    )
  )

(def system (ig/init pms.server/sysconf))

(defn q
  [query-string]
  (let [schema (:game/cgg-schema system)]
    (-> (lacinia/execute schema query-string nil nil)
        sc/simplify)
    )
  )
