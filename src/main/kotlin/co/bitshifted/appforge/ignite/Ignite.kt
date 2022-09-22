/*
 *
 *  * Copyright (c) 2020-2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite

import co.bitshifted.appforge.ignite.ctrl.ControllerRegistry
import co.bitshifted.appforge.ignite.persist.ProjectPersistenceData
import co.bitshifted.appforge.ignite.ui.UIRegistry
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import java.awt.SplashScreen

class Ignite : Application() {

    override fun start(stage: Stage?) {
        ProjectPersistenceData.init()
        ControllerRegistry.registerControllers()
        UIRegistry.loadComponents()
        UIRegistry.setMainWindow(stage ?: throw Exception("Stage can not be null"))


        val scene = Scene(UIRegistry.getComponent(UIRegistry.MAIN_PAGE), 900.0, 700.0)
        stage.title = "Ignite"
        stage.scene = scene
        stage.show()

        val splash = SplashScreen.getSplashScreen() ?: return
        splash.close()
    }


}

fun main(args : Array<String>) {
    Application.launch(Ignite::class.java, *args)
}