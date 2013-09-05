(ns cljs-node-webkit-examples.menus
  (:use [domina :only [by-id set-style!]]
        [domina.events :only [listen! prevent-default]]))

(def gui (js/require "nw.gui"))


;; menu1 functions ------------------------------------------------------------
(defn create-submenu
  []
  (let [submenu (.-Menu gui)
        menu-item (.-MenuItem gui)]
    (doto (new submenu)
      (.append (new menu-item (clj->js {"type" "checkbox" "label" "box1"})))
      (.append (new menu-item (clj->js {"type" "checkbox" "label" "box2"})))
      (.append (new menu-item (clj->js {"type" "checkbox" "label" "box3"})))
      (.append (new menu-item (clj->js {"type" "checkbox" "label" "box4"}))))))

(defn create-menu1 [submenu]
  (let [menu (.-Menu gui)
        menu-item (.-MenuItem gui)]
    (doto (new menu)
      (.append (new menu-item (clj->js {"icon" "imgs/cut.png" "label" "Cut"})))
      (.append (new menu-item (clj->js {"icon" "imgs/edit.png" "label" "Edit"})))
      (.append (new menu-item (clj->js {"icon" "imgs/email.png" "label" "Email"})))
      (.append (new menu-item (clj->js {"icon" "imgs/play.png" "label" "Play"})))
      (.append (new menu-item (clj->js {"icon" "imgs/tick.png" "label" "Tick"})))
      (.append (new menu-item (clj->js {"type" "separator"})))
      (.append (new menu-item (clj->js {"icon" "imgs/disk.png" "label" "Disk"
                                        "submenu" submenu}))))))


;; menu2 functions ------------------------------------------------------------
(def last-one (atom nil))

(defn flip
  [menu-item info-item]
  (do
    (when @last-one
      (swap! last-one #(doto %
        (aset "checked" false)
        (aset "enabled" true))))
  (reset! last-one menu-item)
  (aset menu-item "enabled" false)
  (aset info-item "label" (str "I love " (aget menu-item "label")))))

(defn attach-click-listeners 
  [menu info-item]
  (dotimes [i 4]
    (let [menu-item (nth (.-items menu) i)] 
      (aset menu-item "click" #(flip menu-item info-item)))))

(defn create-menu2 []
  (let [menu (.-Menu gui)
        menu-item (.-MenuItem gui)
        info-item (new menu-item (clj->js {"label" "Which fruit do I love?"}))]
    (doto (new menu)
      (.append (new menu-item (clj->js {"type" "checkbox" "label" "Apple"})))
      (.append (new menu-item (clj->js {"type" "checkbox" "label" "Banana"})))
      (.append (new menu-item (clj->js {"type" "checkbox" "label" "Strawberry"})))
      (.append (new menu-item (clj->js {"type" "checkbox" "label" "Pear"})))
      (.append (new menu-item (clj->js {"type" "separator"})))
      (.append info-item)
      (attach-click-listeners info-item))))


;; menu3 functions ------------------------------------------------------------
(defn change-color []
  (this-as this
           (set-style! (by-id "area-3") :background-color (.-label this))))

(defn create-menu3 []
  (let [menu (.-Menu gui)
        menu-item (.-MenuItem gui)
        menu3 (new menu)
        colors ["#000000" "#FF0000" "#00FF00" "#0000FF" "#FFFF00" "#00FFFF" "#FF00FF"
                 "#C0C0C0" "#FFFFFF"]]
    (doall
      (map 
        #(.append menu3 (new menu-item (clj->js {"label" % "click" change-color}))) 
        colors))
    menu3))


;; main -----------------------------------------------------------------------
(defn menu-listener
  [event menu]
  (do
    (prevent-default event)
    (.popup menu (:x event) (:y event))
    false))

(defn init []
  (let [submenu (create-submenu)
        menu1 (create-menu1 submenu)
        menu2 (create-menu2)
        menu3 (create-menu3)]
    (do
      (listen! (by-id "area-1") :contextmenu #(menu-listener % menu1))
      (listen! (by-id "area-2") :contextmenu #(menu-listener % menu2))
      (listen! (by-id "area-3") :contextmenu #(menu-listener % menu3))
      (.show (.Window.get gui)))))

(listen! js/window :load init)



