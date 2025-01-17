package com.simplemobiletools.commons.extensions

import com.simplemobiletools.commons.helpers.*
import com.simplemobiletools.commons.models.FileDirItem
import java.io.File

fun File.isMediaFile() = absolutePath.isMediaFile()
fun File.isGif() = absolutePath.endsWith(".gif", true)
fun File.isVideoFast() = videoExtensions.any { absolutePath.endsWith(it, true) }
fun File.isImageFast() = photoExtensions.any { absolutePath.endsWith(it, true) }
fun File.isAudioFast() = audioExtensions.any { absolutePath.endsWith(it, true) }
fun File.isRawFast() = rawExtensions.any { absolutePath.endsWith(it, true) }
fun File.isSvg() = absolutePath.isSvg()
fun File.isPortrait() = absolutePath.isPortrait()

fun File.isImageSlow() = absolutePath.isImageFast() || getMimeType().startsWith("image")
fun File.isVideoSlow() = absolutePath.isVideoFast() || getMimeType().startsWith("video")
fun File.isAudioSlow() = absolutePath.isAudioFast() || getMimeType().startsWith("audio")

fun File.getMimeType() = absolutePath.getMimeType()

fun File.getProperSize(countHiddenItems: Boolean): Long {
    return if (isDirectory) {
        getDirectorySize(this, countHiddenItems)
    } else {
        length()
    }
}

private fun getDirectorySize(dir: File, countHiddenItems: Boolean): Long {
    var size = 0L
    if (dir.exists()) {
        val files = dir.listFiles()
        if (files != null) {
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    size += getDirectorySize(files[i], countHiddenItems)
                } else if (!files[i].isHidden && !dir.isHidden || countHiddenItems) {
                    size += files[i].length()
                }
            }
        }
    }
    return size
}

fun File.getFileCount(countHiddenItems: Boolean): Int {
    return if (isDirectory) {
        getDirectoryFileCount(this, countHiddenItems)
    } else {
        1
    }
}

private fun getDirectoryFileCount(dir: File, countHiddenItems: Boolean): Int {
    var count = -1
    if (dir.exists()) {
        val files = dir.listFiles()
        if (files != null) {
            count++
            for (i in files.indices) {
                val file = files[i]
                if (file.isDirectory) {
                    count++
                    count += getDirectoryFileCount(file, countHiddenItems)
                } else if (!file.isHidden || countHiddenItems) {
                    count++
                }
            }
        }
    }
    return count
}

fun File.getDirectChildrenCount(countHiddenItems: Boolean) = listFiles()?.filter { if (countHiddenItems) true else !it.isHidden }?.size ?: 0

fun File.toFileDirItem() = FileDirItem(absolutePath, name, File(absolutePath).isDirectory, 0, length(), lastModified())

fun File.containsNoMedia() = isDirectory && File(this, NOMEDIA).exists()

fun File.doesThisOrParentHaveNoMedia(): Boolean {
    var curFile = this
    while (true) {
        if (curFile.containsNoMedia()) {
            return true
        }
        curFile = curFile.parentFile ?: break
        if (curFile.absolutePath == "/") {
            break
        }
    }
    return false
}
