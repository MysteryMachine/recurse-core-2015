(ns recurse-core-2015.data.process-data
  (:require [cheshire.core :as cheshire]
            [clojure.string :refer [join]]))

(def raw-complaints (cheshire/parse-stream (clojure.java.io/reader "resources/dohmh_complaints.json")))
(def complaints-columns ["DBA" "CUISINE DESCRIPTION" "BUILDING" "STREET" "ZIPCODE"])

(defn get-column-names [table]
  (as-> table $
    (get-in $ ["meta" "view" "columns"])
    (map #(get % "name") $)))

(defn filter-by-columns [table column-names]
  (map #(select-keys (zipmap (get-column-names table) %)
                     column-names)
       (get table "data")))

(def filtered-complaints
  (let [complaints-columns (get-column-names raw-complaints)
        column-idxs        (map #(.indexOf complaints-columns %) complaints-columns)]
    (sequence (map #(map (partial nth %) column-idxs))
              (get raw-complaints "data"))))

(def unique-complaints (set filtered-complaints))
(def complaints (map #(hash-map :title (nth % 0)
                               :cuisine (nth % 1)
                               :address (join " " (map (partial nth %) [3 2]))
                               :zip (nth % 4))
                     unique-complaints))
