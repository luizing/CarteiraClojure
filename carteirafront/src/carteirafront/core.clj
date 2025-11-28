(ns carteirafront.core
  (:require
   [cheshire.core :as json]
   [clj-http.client :as http-client])
  (:gen-class))



;;Variaveis
(def APIBASEURL "http://localhost:3000") ;;mudar para .env

;;Common
(defn imprimirConsulta [vetor]
  (println "\n+=+=+=+=+=+=+=+=+=+=+=+=+=+")
  (println "Acao:"     (nth vetor 0))
  (println "Abertura:" (nth vetor 1))
  (println "Alta:"     (nth vetor 2))
  (println "Baixa:"    (nth vetor 3))
  (println "Preco:"    (nth vetor 4))
  (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+\n")
  )

;;"controller"
(defn consultarAcao [symbol]
  (let [url (str APIBASEURL "/acao/" symbol)
        response (http-client/get url {:as :json})


        acao (get-in response [:body :acao])
        abertura (get-in response [:body :abertura])
        alta (get-in response [:body :alta])
        baixa (get-in response [:body :baixa])
        preco (get-in response [:body :preco])
        ]
    [acao abertura alta baixa preco]
    )
)


  (defn comprarAcao [symbol qtd]
    (http-client/post (str APIBASEURL "/compra") {:body (json/encode {:symbol symbol
                                                                      :quantidade qtd})}))

  (defn venderAcao [symbol qtd]
    (http-client/post (str APIBASEURL "vende") {:body (json/encode {:symbol symbol
                                                                    :quantidade qtd})}))
  (defn exibirExtrato []
    (http-client/get (str APIBASEURL "extrato")))

  (defn exibirSaldo []
    (http-client/get (str APIBASEURL "saldo")))

  ;;Menus
  (defn consultarAcao-MENU []
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
    (println "=+= Digite a acao que deseja consultar: =+=")
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+\n")
    (imprimirConsulta (consultarAcao (read))))

  (defn comprarAcao-MENU []
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
    (println "=+= Digite a acao que deseja comprar, depois a quantidade: =+=")
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+\n")
    (let [acao (read)
          qtd (read)]
      (comprarAcao acao qtd)))

  (defn venderAcao-MENU []
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+")
    (println "=+= Digite a acao que deseja vender, depois a quantidade: =+=")
    (println "+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+\n")
    (let [acao (read)
          qtd (read)]
      (venderAcao acao qtd)))


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
        (= opcao 2) (comprarAcao-MENU)
        (= opcao 3) (venderAcao-MENU)
        (= opcao 4) (exibirExtrato)
        (= opcao 5) (exibirSaldo)
        (= opcao 6) (println (System/getenv "API_URL"))
        :else (println "..."))
      (recur)))

  (defn -main
    [& args]
    (menu))
