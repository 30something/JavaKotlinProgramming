package com._30something.calc_gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com._30something.lib_calc.*

@Composable
@Preview
fun app() {
    var expression: String by remember { mutableStateOf("") }
    var inputError: Boolean by remember { mutableStateOf(false) }
    var inputErrorText: String by remember { mutableStateOf("Error occurred...") }
    var resultText: String by remember { mutableStateOf("") }
    var treeText: String by remember { mutableStateOf("") }
    var treeDepthText: String by remember { mutableStateOf("") }
    var reconstructedExprText: String by remember { mutableStateOf("") }
    var cursorPos: Int by remember { mutableStateOf(0) }
    var literalsSet = mutableSetOf<String>()
    val literalsMap = mutableStateMapOf<String, Double>()
    val checkerMap = mutableStateMapOf<String, TextFieldValue>()

    @Composable
    fun createGUIButton(symbol: Char) {
        Button(
            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
            onClick = {
                expression += symbol
                cursorPos = expression.length
            }
        ) {
            Text(symbol.toString())
        }
    }

    @Composable
    fun createAdditionalResultFields(value: String, label: String) {
        TextField(
            modifier = Modifier.fillMaxWidth(fraction = 0.55f).padding(all = 2.dp),
            value = value,
            onValueChange = {},
            label = { Text(label, fontSize = 10.sp) },
            singleLine = true,
            readOnly = true
        )
    }

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = 1.5f,
            fontScale = 1.5f
        )
    ) {
        MaterialTheme {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(all = 2.dp)
                        .fillMaxSize()
                ) {
                    Column {
                        TextField(
                            modifier = Modifier.padding(all = 2.dp).fillMaxWidth(),
                            value = TextFieldValue(
                                expression,
                                TextRange(cursorPos),
                            ),
                            onValueChange = {
                                expression = it.text
                                cursorPos = it.selection.start
                            },
                            trailingIcon = {
                                if (inputError) {
                                    Icon(
                                        Icons.Default.Warning, "Warning",
                                        tint = MaterialTheme.colors.error
                                    )
                                }
                            },
                            placeholder = { Text("Enter expression", fontSize = 10.sp) },
                            singleLine = true,
                            isError = inputError
                        )
                        if (inputError) {
                            Text(
                                text = inputErrorText,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.subtitle1,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(all = 2.dp)
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxHeight(fraction = 0.78f)
                        .fillMaxWidth()
                ) {
                    Row {
                        Column {
                            TextField(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 10.dp)
                                    .fillMaxWidth(fraction = 0.405f)
                                    .fillMaxHeight(0.2f),
                                value = resultText,
                                onValueChange = {},
                                label = { Text("Result:", fontSize = 10.sp) },
                                singleLine = true,
                                readOnly = true
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(all = 2.dp)
                            .weight(1f)
                    ) {
                        createGUIButton('(')
                        createGUIButton(')')
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression = ""
                                inputErrorText = ""
                                resultText = ""
                                treeText = ""
                                treeDepthText = ""
                                reconstructedExprText = ""
                                inputError = false
                                literalsSet.clear()
                                checkerMap.clear()
                                literalsMap.clear()
                            }
                        ) {
                            Text("C")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                val parser = ParserImpl()
                                val debugVisitor = DebugRepresentationExpressionVisitor()
                                val depthVisitor = DepthVisitor()
                                val toStringVisitor = ToStringVisitor.INSTANCE
                                val requestVisitor = RequestVisitor()
                                try {
                                    val expr = parser.parseExpression(expression)
                                    treeText = expr.accept(debugVisitor) as String
                                    treeDepthText = expr.accept(depthVisitor).toString()
                                    reconstructedExprText = expr.accept(toStringVisitor) as String
                                    expr.accept(requestVisitor)
                                    literalsSet = requestVisitor.variablesSet
                                    if (checkerMap.size < literalsSet.size) {
                                        throw Exception("Please, input all variables (use scroll if needed)")
                                    }
                                    literalsMap.clear()
                                    for (element in checkerMap) {
                                        val value = element.value.text
                                        val key = element.key
                                        try {
                                            literalsMap[key] = value.toDouble()
                                        } catch (exc: Exception) {
                                            throw Exception("Unable to convert input string " +
                                                    "'$value' to value for variable '$key'")
                                        }
                                    }
                                    val computeVisitor = ComputeExpressionVisitor(literalsMap)
                                    val result: Double = expr.accept(computeVisitor) as Double
                                    resultText = result.toString()
                                    inputError = false
                                    inputErrorText = ""
                                } catch (exc: Exception) {
                                    inputError = true
                                    inputErrorText = exc.message.toString()
                                }
                            }
                        ) {
                            Text("=")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(all = 2.dp)
                            .weight(1f)
                    ) {
                        createGUIButton('1')
                        createGUIButton('2')
                        createGUIButton('3')
                        createGUIButton('/')
                    }
                    Row(
                        modifier = Modifier
                            .padding(all = 2.dp)
                            .weight(1f)
                    ) {
                        createGUIButton('4')
                        createGUIButton('5')
                        createGUIButton('6')
                        createGUIButton('*')
                    }
                    Row(
                        modifier = Modifier
                            .padding(all = 2.dp)
                            .weight(1f)
                    ) {
                        createGUIButton('7')
                        createGUIButton('8')
                        createGUIButton('9')
                        createGUIButton('-')
                    }
                    Row(
                        modifier = Modifier
                            .padding(all = 2.dp)
                            .weight(1f)
                    ) {
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression = expression.dropLast(1)
                            }
                        ) {
                            Text("←")
                        }
                        createGUIButton('0')
                        createGUIButton('.')
                        createGUIButton('+')
                    }
                }
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(25.dp, 10.dp),
                ) {
                    Column {
                        createAdditionalResultFields(treeText, "Debug tree: ")
                        createAdditionalResultFields(treeDepthText, "Tree depth: ")
                        createAdditionalResultFields(reconstructedExprText, "Reconstructed expression: ")
                        LazyColumn(
                            modifier = Modifier.fillMaxHeight(0.64f).padding(all = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (element in literalsSet) {
                                item {
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(fraction = 0.55f),
                                        value = checkerMap[element] ?: TextFieldValue(),
                                        onValueChange = {
                                            checkerMap[element] = it
                                        },
                                        label = { Text("$element = ") },
                                        singleLine = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Calculator GUI",
        state = WindowState(size = DpSize(1024.dp, 768.dp)),
        resizable = false
    ) {
        app()
    }
}
