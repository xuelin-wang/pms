(defproject pms "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [com.walmartlabs/lacinia-pedestal "0.7.0"]
                 [io.aviso/logging "0.3.1"]
                 [redis.clients/jedis "2.9.0"]
                 [circleci/clj-yaml "0.5.6"]
                 [integrant "0.7.0-alpha2"]
                 ]
  :aot :all
  :main xl.pms.core

  )
