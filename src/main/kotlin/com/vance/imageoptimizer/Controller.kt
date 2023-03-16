package com.vance.imageoptimizer

import javafx.application.Platform
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.DirectoryChooser
import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.file.OpenOption
import java.nio.file.Path

class Controller {


    @FXML
    private lateinit var chooseFolderButton: Button

    @FXML
    private lateinit var chosenFolderLabel: Label

    @FXML
    private lateinit var startOptimizeButton: Button

    @FXML
    private lateinit var fileCountLabel: Label

    @FXML
    private lateinit var progressBar: ProgressBar

    @FXML
    private lateinit var progressLabel: Label

    @FXML
    private lateinit var delayTextField: TextField

    @FXML
    private lateinit var currentFileLabel: Label

    @FXML
    private lateinit var rewriteRadioButton: RadioButton

    private lateinit var selectedDirectory: File

    private var fileCount = 0
    private var optFileCount = 0
    private var optTotalSize = 0

    private lateinit var client: HttpClient

    private val endPointUrl = "https://tinypng.com/backend/opt/shrink"

    @FXML
    fun initialize() {
        client = HttpClient.newBuilder().build()

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
        fileCountLabel.text = "Найдено ${getFileCount()} файлов"
    }

    private fun startOptimize() {
        if (selectedDirectory.exists() && selectedDirectory.isDirectory) {
            val arr = selectedDirectory.listFiles()
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
            Thread {
                recursiveWalk(arr, 0)
                showSuccessDialog()
            }.start()

        }
    }

    private fun getFileCount(): Int {
        if (selectedDirectory.exists() && selectedDirectory.isDirectory) {
            fileCount = 0
            optFileCount = 0
            optTotalSize = 0
            recursiveWalkCount(selectedDirectory.listFiles(), 0);
        }
        return fileCount
    }

    private fun recursiveWalk(arr: Array<File>, level: Int) {
        for (f in arr) {
            if (f.isFile) {
                optimizeImage(f)
                if (optFileCount != 0) Thread.sleep(delayTextField.text.toLong())
            } else if (f.isDirectory) {

                recursiveWalk(f.listFiles(), level + 1)
            }
        }
    }

    private fun recursiveWalkCount(arr: Array<File>, level: Int) {
        for (f in arr) {
            if (f.isFile) {
                if (f.extension == "png") {
                    fileCount++
                }
            } else if (f.isDirectory) {
                recursiveWalkCount(f.listFiles(), level + 1)
            }
        }
    }


    private fun optimizeImage(file: File) {
        if (file.extension == "png") {
            Platform.runLater { currentFileLabel.text = file.name }
            val request = HttpRequest.newBuilder()
                .uri(URI("https://tinypng.com/backend/opt/shrink"))
                .header("User-Agent", "PostmanRuntime/7.31.3")
                .POST(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                .build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            val imageUrl = response.body().split("\"url\":\"").last().dropLast(3)
            val sourceSize =
                response.body().split("\"size\":")[1].split(",").first().toInt()
            val endSize =
                response.body().split("\"size\":")[2].split(",").first().toInt()

            optTotalSize += (sourceSize - endSize)

            val imgRequest = HttpRequest.newBuilder()
                .uri(URI(imageUrl))
                .header("User-Agent", "PostmanRuntime/7.31.3")
                .build()

            val newPath = if (rewriteRadioButton.isSelected) "${file.parentFile.toPath()}\\${file.nameWithoutExtension}.png" else "${file.parentFile.toPath()}\\X_${file.nameWithoutExtension}.png"

            if (rewriteRadioButton.isSelected) file.delete()
            client.send(
                imgRequest,
                HttpResponse.BodyHandlers.ofFile(Path.of(newPath))
            )
            optFileCount++
            Platform.runLater {
                updateProgress()
            }
        }
    }

    private fun updateProgress() {
        progressBar.progress = optFileCount.toDouble() / fileCount
        progressLabel.text = "Оптимизировано $optFileCount из $fileCount"
    }

    private fun showSuccessDialog() {
        Platform.runLater {
            val alert = Alert(Alert.AlertType.INFORMATION)
            alert.title = "Успех"
            alert.contentText =
                "Было оптимизировано ${fileCount} файлов (- ${Math.round((optTotalSize.toDouble() / (1024 * 1024)) * 100.0) / 100.0} MB)"
            alert.showAndWait()
        }
    }

    fun roundDouble() {

    }
}