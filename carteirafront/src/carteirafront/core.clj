(ns carteirafront.core
  (:require
   [cheshire.core :as json]
   [clj-http.client :as http-client]
   [clojure.pprint :refer [pprint]])
  (:gen-class))

(def APIBASEURL "http://localhost:3002")

(defn urlCreator [operador symbol]
  (str APIBASEURL operador symbol)
  )

(defn consultarAcao [symbol]
  (json/parse-string (:body (http-client/get (urlCreator "/acao/" symbol))) true))

(defn imprimirDesrealização[json]
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (pprint json)
  )

(defn consultarAcao-MENU []
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "=+= Digite a acao que deseja consultar: =+=")
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+\n")
  (imprimirDesrealização (consultarAcao (read)))
  )


(defn menu []
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "=+= Gerenciador de Carteira de Acoes =+=")
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "\nDigite a opcao desejada:")
  (println "1 - Consultar acao")
  (println "2 - Comprar acao")
  (println "3 - Vender acao")
  (println "4 - Exibir extrato")
  (println "5 - Exibir saldo\n")

  (let
   [opcao (read)]
    (cond
      (= opcao 1) (consultarAcao-MENU)
      ;(= opcao 2) (comprar)
      ;(= opcao 3) (vender)
      ;(= opcao 4) (exibir-extrato)
      ;(= opcao 5) (exibir-saldo)
      :else (println "..."))
    (recur)))

(defn -main
  [& args]
  (menu)
  )
