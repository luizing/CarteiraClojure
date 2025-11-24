(ns backend.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/acao" [] "Faz a requisição para API externa e busca os dados da ação enviadas via parametros da url ou corpo da requisição ou link")
  (POST "/compra/acao" [] "Faz o registro de uma nova ação na carteira do usuario - Checa os dados da ação naquele momento")
  (POST "/vende/acao" [] "Faz o registro da venda de uma ação na carteira do usuario - Adiciona os fundos a carteira e remove a ação da lista de ações")
  (GET "/extrato" [] "Retorna o registro de compras e vendas em determinado período (enviado na requisição? enviar todos os registros divididos em períodos? dos ultimos x meses?)") 
  (GET "/saldo" [] "Obter saldo da carteira")
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
