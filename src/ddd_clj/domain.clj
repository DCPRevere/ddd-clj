(ns ddd-clj.core
  (:gen-class))


(defprotocol Aggregate)

(defrecord Txn [agg-id payer payee amount events]
  Aggregate)




(defprotocol Event)

(defrecord TxnCreated [agg-id data]
  Event)

(defrecord TxnAmountChanged [agg-id data])


(defprotocol DomainRepo
  (get-by-id [dr id])
  (save [dr agg]))

(def eg-txns
  [{:payer "Daniel"
    :payee "Tan"
    :amount 315
    :agg-id "123"}])

(defrecord EdnStore []
  DomainRepo
  (get-by-id [dr id]
    "Rebuilds the aggregate from its events."
    (map->Txn
     (first (filter #(= id (:agg-id %)) eg-txns))))
  (save [dr agg] nil))

;; (defrecord EventStore []
;;   DomainRepo
;;   (get-by-id [dr id] (...))
;;   (save [dr agg] (...)))





(defmulti apply-event
  (fn [agg event]
    [(type agg) (type event)]))

(defmethod apply-event [ddd_clj.core.Txn ddd_clj.core.TxnAmountChanged]
  [agg event])



(defn change-txn-amount [txn amount]
  (assoc txn :amount amount))

(defprotocol Command
  (handle [command repo]))

(defrecord ChangeTxnAmount [agg-id amount]
  Command
  (handle [command repo]
    (let [txn (get-by-id repo (:agg-id command))]
      (change-txn-amount txn (:amount command)))))

(handle (->ChangeTxnAmount "123" 263) (->EdnStore))

