package com._30something.calc_gui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com._30something.lib_calc.*
import java.util.*

@Composable
@Preview
fun app() {
    var expression: String by remember { mutableStateOf("") }

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
                    TextField(
                        modifier = Modifier.padding(all = 2.dp).fillMaxWidth(),
                        value = expression,
                        onValueChange = {
                            expression = it
                        },
                        placeholder = { Text("Enter expression", fontSize = 10.sp) },
                        singleLine = true
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxHeight(fraction = 0.8f)
                        .fillMaxWidth()
                ) {
                    TextField(
                        modifier = Modifier.padding(all = 4.dp).fillMaxWidth(fraction = 0.52f),
                        value = "",
                        onValueChange = {

                        },
                        label = { Text("Result:", fontSize = 10.sp) },
                        readOnly = true
                    )
                    Row(
                        modifier = Modifier
                            .padding(all = 2.dp)
                            .weight(1f)
                    ) {
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "("
                            }
                        ) {
                            Text("(")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += ")"
                            }
                        ) {
                            Text(")")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression = ""
                            }
                        ) {
                            Text("C")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                // TODO
                                val parser = ParserImpl()
                                val debugVisitor = DebugRepresentationExpressionVisitor()
                                val depthVisitor = DepthVisitor()
                                val toStringVisitor = ToStringVisitor.INSTANCE
                                var correctInput = false
                                try {
                                    val expr = parser.parseExpression(expression)
                                    print("Tree: ")
                                    println(expr.accept(debugVisitor) as String)
                                    print("Expr-tree depth: ")
                                    println(expr.accept(depthVisitor))
                                    print("Reconstructed expression: ")
                                    println(expr.accept(toStringVisitor) as String)
                                    //val requestVisitor = RequestVisitor(inpt)
//                                    expr.accept(requestVisitor)
//                                    val variablesMap = requestVisitor.variablesMap
//                                    val computeVisitor = ComputeExpressionVisitor(variablesMap)
//                                    print("Result: ")
//                                    println(expr.accept(computeVisitor))
                                    correctInput = true
                                } catch (exc: Exception) {
                                    println(exc.message)
                                    println("Please, input the expression again")
                                    correctInput = false
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
                            }
                        ) {
                            Text("1")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "2"
                            }
                        ) {
                            Text("2")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "3"
                            }
                        ) {
                            Text("3")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "/"
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
                            }
                        ) {
                            Text("4")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "5"
                            }
                        ) {
                            Text("5")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "6"
                            }
                        ) {
                            Text("6")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "*"
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
                            }
                        ) {
                            Text("7")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "8"
                            }
                        ) {
                            Text("8")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "9"
                            }
                        ) {
                            Text("9")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "-"
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
                            }
                        ) {
                            Text("0")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "."
                            }
                        ) {
                            Text(".")
                        }
                        Button(
                            modifier = Modifier.wrapContentSize().padding(all = 2.dp),
                            onClick = {
                                expression += "+"
                            }
                        ) {
                            Text("+")
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
        resizable = false
    ) {
        app()
    }
}
