(ns carteirafront.env
  (:require [clojure.string :as str]))

(defn carregar-env
  ([] (carregar-env "../.env"))
  ([caminho]
   (->> (slurp caminho)
        str/split-lines
        (remove #(or (str/blank? %)
                     (str/starts-with? % "#")))
        (map #(str/split % #"=" 2))
        (map (fn [[k v]] [(keyword k) v]))
        (into {}))))
