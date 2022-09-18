/*
 *
 *  * Copyright (c) 2020-2022  Bitshift D.O.O (http://bitshifted.co)
 *  *
 *  * This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 */

package co.bitshifted.appforge.ignite.ctrl

import co.bitshifted.appforge.ignite.model.DependencyManagementType
import co.bitshifted.appforge.ignite.model.IgniteConfig
import co.bitshifted.appforge.ignite.model.Project
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.ButtonType
import javafx.scene.control.ComboBox
import javafx.scene.control.TextField
import javafx.util.Callback

class NewProjectDialogController {

    @FXML
    private lateinit  var projectNameField : TextField
    @FXML
    private lateinit var projectLocationField : TextField
    @FXML
    private lateinit var dependencyCombo : ComboBox<DependencyManagementType>
    @FXML
    private lateinit var browseButton : Button

    @FXML
    fun initialize() {
        dependencyCombo.items?.addAll(DependencyManagementType.values())
        dependencyCombo.selectionModel?.selectFirst()
    }



    fun getResultConverter() : Callback<ButtonType, Project?> {
        return object : Callback<ButtonType, Project?> {
            override fun call(btype: ButtonType?): Project? {
                if(btype == ButtonType.OK) {
                    return createProject()
                }
                return null
            }
        }
    }

    fun validateInput() : Boolean {
        return (projectLocationField.text?.isNotEmpty() == true && projectNameField.text?.isNotEmpty() == true)
    }

    private fun createProject() : Project {
         val project = Project(IgniteConfig(), projectLocationField.text, projectNameField.text)
//        project.name = projectNameField.text
//        project.location = projectLocationField.text
        project.dependencyManagementType = dependencyCombo.value
        return project
    }

}
