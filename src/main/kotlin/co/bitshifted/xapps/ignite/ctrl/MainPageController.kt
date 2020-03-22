/*
 * Copyright (c) 2020. Bitshift (http://bitshifted.co)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package co.bitshifted.xapps.ignite.ctrl

import co.bitshifted.xapps.ignite.model.Project
import co.bitshifted.xapps.ignite.model.ProjectItemType
import co.bitshifted.xapps.ignite.model.RuntimeData
import co.bitshifted.xapps.ignite.persist.ProjectPersistenceData
import co.bitshifted.xapps.ignite.persist.XMLPersister
import co.bitshifted.xapps.ignite.ui.ProjectTreeCellFactory
import co.bitshifted.xapps.ignite.ui.ProjectTreeItem
import co.bitshifted.xapps.ignite.ui.UIRegistry
import javafx.application.Platform
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ListChangeListener
import javafx.fxml.FXML
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.layout.AnchorPane
import org.kordamp.ikonli.fontawesome.FontAwesome
import org.kordamp.ikonli.javafx.FontIcon

class MainPageController : ListChangeListener<Project>  {

    private val ANCHOR_DISTANCE = 10.0
    private val TREE_ICONS_SIZE = 17

    @FXML
    lateinit private var detailsPane : AnchorPane
    @FXML
    lateinit private var projectTree : TreeView<ProjectTreeItem>

    private val treeRootIcon : FontIcon
    private val projectIcon : FontIcon
    private val applicationIcon : FontIcon

    init {
        treeRootIcon = FontIcon(FontAwesome.POWER_OFF)
        treeRootIcon.iconSize = TREE_ICONS_SIZE
        projectIcon = FontIcon(FontAwesome.BRIEFCASE)
        projectIcon.iconSize = 17
        applicationIcon = FontIcon(FontAwesome.FLASK)
        projectIcon.iconSize = 17
    }

    @FXML
    fun initialize() {
        createProjectTree();
        detailsPane.children.add(UIRegistry.getComponent(UIRegistry.START_PANE))
        RuntimeData.projectList.addListener(this)
    }

    private fun createProjectTree() {
        treeRootIcon.iconSize = 17
        projectTree.cellFactory = ProjectTreeCellFactory()
        val root = TreeItem(ProjectTreeItem(ProjectItemType.ROOT), treeRootIcon)
        root.expandedProperty().set(true)
        projectTree.root = root

        Platform.runLater {
            for(location in ProjectPersistenceData.loadProjectLocations()) {
                val proj = XMLPersister.loadProject(location)
                projectTree.root?.children?.add(createProjectNode(proj))
            }
        }

        projectTree.selectionModel?.selectedItemProperty()?.addListener(object:
            ChangeListener<TreeItem<ProjectTreeItem>> {
            override fun changed(
                observable: ObservableValue<out TreeItem<ProjectTreeItem>>?,
                oldValue : TreeItem<ProjectTreeItem>?,
                newValue : TreeItem<ProjectTreeItem>?
            ) {
                RuntimeData.selectedProjectItem.value = newValue?.value
                when(newValue?.value?.type) {
                    ProjectItemType.ROOT -> {
                        detailsPane.children.clear()
                        detailsPane.children.add(UIRegistry.getComponent(UIRegistry.START_PANE))
                        AnchorPane.setTopAnchor(detailsPane.children[0], 0.0)
                        AnchorPane.setLeftAnchor(detailsPane.children[0], 0.0)
                        AnchorPane.setRightAnchor(detailsPane.children[0], 0.0)
//                        detailsPane.bottom = null
                    }
                    ProjectItemType.PROJECT -> {
                        setupDetailsPane(UIRegistry.PROJECT_INFO_PANE)
                    }
                    ProjectItemType.APPLICATION -> {
                        setupDetailsPane(UIRegistry.APP_INFO_PANE)
                    }

                }
            }
        })
    }

    override fun onChanged(change: ListChangeListener.Change<out Project>?) {
        while (change?.next() == true && change.wasAdded() == true) {
            for(project in (change.addedSubList ?: emptyList())) {
                projectTree.root?.children?.add(createProjectNode(project))
                ProjectPersistenceData.saveProject(project)
            }
        }
    }

    private fun createProjectNode(project : Project) : TreeItem<ProjectTreeItem> {
        val projectNode = TreeItem(ProjectTreeItem(ProjectItemType.PROJECT, project), projectIcon)
        projectNode.children.add(TreeItem(ProjectTreeItem(ProjectItemType.APPLICATION, project), applicationIcon))

        return projectNode
    }

    private fun setupDetailsPane(name : String) {
        detailsPane.children.clear()
        detailsPane.children.addAll(UIRegistry.getComponent(name), UIRegistry.getComponent(UIRegistry.PROJECT_BUTTON_BAR))
        AnchorPane.setTopAnchor(detailsPane.children[0], ANCHOR_DISTANCE)
        AnchorPane.setLeftAnchor(detailsPane.children[0], ANCHOR_DISTANCE)
        AnchorPane.setRightAnchor(detailsPane.children[0], ANCHOR_DISTANCE)
        AnchorPane.setBottomAnchor(detailsPane.children[1], ANCHOR_DISTANCE)
    }


}