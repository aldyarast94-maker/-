// Добавка к Android-модулю, который сам запускает модели ONNX (script, tts).
// Подмену на настольную сборку в тестах делает bubblecast.android для всех модулей — ONNX приходит и транзитивно.
dependencies { "implementation"(lib("onnxruntime-android")) }
