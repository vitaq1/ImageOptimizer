package com.vance.imageoptimizer

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.DirectoryChooser
import java.io.File

class Controller {


    @FXML
    private lateinit var chooseFolderButton: Button

    @FXML
    private lateinit var chosenFolderLabel: Label

    @FXML
    fun initialize() {

        chooseFolderButton.setOnAction {
            selectInitialDir()
        }

    }

    private fun selectInitialDir(){
        val directoryChooser = DirectoryChooser()
        val selectedDirectory: File? = directoryChooser.showDialog(chooseFolderButton.scene.window)
        if (selectedDirectory == null) {
            //No Directory selected
        } else {
            if (selectedDirectory.exists() && selectedDirectory.isDirectory) {
                 val arr = selectedDirectory.listFiles();
                System.out.println(
                    "**********************************************");
                System.out.println(
                    "Files from main directory : " + selectedDirectory);
                System.out.println(
                    "**********************************************");
                // Calling recursive method
                recursiveWalk(arr, 0);
            }
        }
    }

    private fun recursiveWalk(arr: Array<File>, level: Int) {
        // for-each loop for main directory files
        for (f in arr) {
            // tabs for internal levels
            for (i in 0 until level) print("\t")
            if (f.isFile) {
                println(f.name)
            } else if (f.isDirectory) {
                println("[" + f.name + "]")
                // recursion for sub-directories
                recursiveWalk(f.listFiles(), level + 1)
            }
        }
    }
}