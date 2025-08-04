package org.example.app

import java.io.File
import java.io.InputStream

object LoadProgramFromD5700 {
    fun loadProgram(
        memoryDriver: MemoryDriver,
        memoryIndex: Int,
        startingIndex: Int,
        fileName: String,
        forceCompile: Boolean = false
    ) {
        val classLoader = javaClass.classLoader
        val resourcePath = "roms/$fileName"
        val fileExtension = File(fileName).extension.lowercase()

        val programBytes: ByteArray

        when {
            fileExtension == "out" && !forceCompile -> {
                // Load precompiled binary directly from resource
                val outStream: InputStream = classLoader.getResourceAsStream(resourcePath)
                    ?: throw IllegalArgumentException("File $resourcePath not found in resources.")
                programBytes = outStream.readBytes()
            }

            fileExtension == "d5700" || forceCompile -> {
                val d5700Stream: InputStream = classLoader.getResourceAsStream(resourcePath)
                    ?: throw IllegalArgumentException("File $resourcePath not found in resources.")

                val lines = d5700Stream.bufferedReader().readLines()
                programBytes = compile(lines)

                // Cache compiled binary in temp directory for later reuse
                val tempOutFile = File("${System.getProperty("java.io.tmpdir")}/${fileNameWithoutExtension(fileName)}.out")
                tempOutFile.writeBytes(programBytes)
            }

            else -> {
                throw IllegalArgumentException("Unsupported file extension: $fileName")
            }
        }

        for (i in programBytes.indices) {
            memoryDriver.write(memoryIndex, programBytes[i], startingIndex + i)
        }
    }

    private fun compile(lines: List<String>): ByteArray {
        val byteList = mutableListOf<Byte>()
        for (line in lines) {
            val trimmed = line.substringBefore('#').trim()
            if (trimmed.length != 4) {
                throw IllegalArgumentException("Invalid instruction length: $trimmed")
            }

            val word = trimmed.toUShort(16)
            byteList.add((word.toInt() shr 8).toByte())     // high byte
            byteList.add((word.toInt() and 0xFF).toByte())  // low byte
        }
        return byteList.toByteArray()
    }

    private fun fileNameWithoutExtension(name: String): String {
        return name.substringBeforeLast('.', name)
    }
}