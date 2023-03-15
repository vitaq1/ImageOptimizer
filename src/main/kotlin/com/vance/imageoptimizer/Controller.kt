package com.vance.imageoptimizer

import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.stage.DirectoryChooser
import kotlinx.coroutines.*
import java.io.File

class Controller {


    @FXML
    private lateinit var chooseFolderButton: Button

    @FXML
    private lateinit var chosenFolderLabel: Label

    @FXML
    private lateinit var startOptimizeButton: Button

    private lateinit var selectedDirectory: File

    @FXML
    fun initialize() {

        chooseFolderButton.setOnAction {
            selectInitialDir()
        }

        startOptimizeButton.setOnAction {
            startOptimize()
        }

    }

    private fun selectInitialDir() {
        val directoryChooser = DirectoryChooser()
        selectedDirectory = directoryChooser.showDialog(chooseFolderButton.scene.window)
        chosenFolderLabel.text = selectedDirectory.absolutePath

    }

    private fun startOptimize() {
        if (selectedDirectory.exists() && selectedDirectory.isDirectory) {
            val arr = selectedDirectory.listFiles();
            System.out.println(
                "**********************************************"
            );
            System.out.println(
                "Files from main directory : " + selectedDirectory
            );
            System.out.println(
                "**********************************************"
            );
            // Calling recursive method
            recursiveWalk(arr, 0);
        }
    }


    private fun recursiveWalk(arr: Array<File>, level: Int) {
        // for-each loop for main directory files
        for (f in arr) {
            // tabs for internal levels
            //for (i in 0 until level) print("\t")
            if (f.isFile) {

                GlobalScope.launch(Dispatchers.Main) {
                    optimizeImage(f)
                }

            } else if (f.isDirectory) {
                //println("[" + f.name + "]")
                // recursion for sub-directories
                recursiveWalk(f.listFiles(), level + 1)
            }
        }
    }

    private fun optimizeImage(file: File) {
        if (file.extension == "png") {
            println("png")
        }
    }
}