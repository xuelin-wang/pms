(ns user
  (:require
    [xl.pms.server]
    [xl.pms.game.schema :as sc]
    [com.walmartlabs.lacinia :as lacinia]
    [integrant.core :as ig]
    )
  )

(comment
  (def system (ig/init (xl.pms.server/get-conf "prod")))

  (defn q
    [query-string]
    (let [schema (:game/cgg-schema system)]
      (-> (lacinia/execute schema query-string nil nil)
          sc/simplify)
      )
    )
  )
