package com._30something.calc_gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.*
import androidx.compose.ui.window.*
import com._30something.lib_calc.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
    var literalsSet: HashSet<String> by remember { mutableStateOf(HashSet()) }
    var literalsMap: HashMap<String, Double> by remember { mutableStateOf(HashMap()) }
    var checkerMap: HashMap<String, String> by remember { mutableStateOf(HashMap()) }

    CompositionLocalProvider(
        LocalDensity provides Density(
            density = 1.5f,
            fontScale = 1.5f,
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
                                    .padding(all = 4.dp)
                                    .fillMaxWidth(fraction = 0.405f)
                                    .fillMaxHeight(0.2f),
                                value = resultText,
                                onValueChange = {},
                                label = { Text("Result:", fontSize = 10.sp) },
                                readOnly = true,
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(all = 2.dp)
                            .weight(1f)
                    ) {
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "("
                                cursorPos = expression.length
                            },
                        ) {
                            Text("(")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += ")"
                                cursorPos = expression.length
                            }
                        ) {
                            Text(")")
                        }
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
                                        println("lllllooooolllll")
                                        inputError = true
                                        inputErrorText = "Please, input all variables (use scroll if needed)"
                                    } else {
                                        println("hahaha")
                                        literalsMap.clear()
                                        for (element in checkerMap) {
                                            try {
                                                literalsMap[element.key] = element.value.toDouble()
                                            } catch (exc: Exception) {
                                                inputError = true
                                                inputErrorText = "Unable to convert input string " + element.value +
                                                        " to value for variable " + element.key +
                                                        ". Please input value again"
                                                throw Exception("ahhahaha")
                                            }
                                        }
                                        println(inputError)
                                        if (!inputError) {
                                            val computeVisitor = ComputeExpressionVisitor(literalsMap)
                                            val result: Double = expr.accept(computeVisitor) as Double
                                            resultText = result.toString()
                                        }
                                    }
                                } catch (exc: Exception) {
                                    inputError = true
                                    inputErrorText = exc.message + ". Please, input the expression again"
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
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "1"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("1")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "2"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("2")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "3"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("3")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "/"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("/")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(all = 2.dp)
                            .weight(1f)
                    ) {
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "4"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("4")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "5"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("5")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "6"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("6")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "*"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("*")
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(all = 2.dp)
                            .weight(1f)
                    ) {
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "7"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("7")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "8"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("8")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "9"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("9")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "-"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("-")
                        }
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
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "0"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("0")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "."
                                cursorPos = expression.length
                            }
                        ) {
                            Text(".")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "+"
                                cursorPos = expression.length
                            }
                        ) {
                            Text("+")
                        }
                    }
                }
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(25.dp, 10.dp),
                ) {
                    Column {
                        TextField(
                            modifier = Modifier.fillMaxWidth(fraction = 0.55f).padding(all = 2.dp),
                            value = treeText,
                            onValueChange = {},
                            label = { Text("Debug tree: ", fontSize = 10.sp) },
                            singleLine = true,
                            readOnly = true
                        )
                        TextField(
                            modifier = Modifier.fillMaxWidth(fraction = 0.55f).padding(all = 2.dp),
                            value = treeDepthText,
                            onValueChange = {},
                            label = { Text("Tree depth: ", fontSize = 10.sp) },
                            singleLine = true,
                            readOnly = true
                        )
                        TextField(
                            modifier = Modifier.fillMaxWidth(fraction = 0.55f).padding(all = 2.dp),
                            value = reconstructedExprText,
                            onValueChange = {},
                            label = { Text("Reconstructed expression: ", fontSize = 10.sp) },
                            singleLine = true,
                            readOnly = true
                        )
                        LazyColumn(
                            modifier = Modifier.fillMaxHeight(0.64f).padding(all = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (element in literalsSet) {
                                var tempText = if (checkerMap.containsKey(element)) checkerMap[element] ?: "" else ""
                                println(tempText)
                                item {
                                    TextField(
                                        modifier = Modifier.fillMaxWidth(fraction = 0.55f),
                                        value = tempText,
                                        onValueChange = { newText: String ->
                                            //tempText = newText
                                            checkerMap[element] = newText
                                        },
                                        placeholder = { Text("$element = ") },
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
