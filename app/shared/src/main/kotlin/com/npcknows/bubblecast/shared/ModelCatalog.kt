package com.npcknows.bubblecast.shared

import java.io.File

/**
 * Файл модели, который приложение скачивает при первом запуске.
 * @param path путь внутри папки моделей на телефоне.
 * @param url откуда скачать (ревизия зафиксирована).
 */
data class ModelFile(
    val path: String,
    val url: String,
    val sha256: String,
    val sizeBytes: Long,
    val license: String,
)

/** Где лежит скачанный файл модели. Даёт сборка app/ (на телефоне) или тест. */
fun interface ModelLocator {
    fun file(model: ModelFile): File
}

/**
 * ЕДИНСТВЕННЫЙ список моделей приложения. Лицензии проверены (см. README модулей script и tts).
 * Детектор пузырей: ogkalu/comic-text-and-bubble-detector (данные обучения не раскрыты — вопрос автору до релиза).
 */
object ModelCatalog {
    val DETECTOR = ModelFile(
        "detector/detector_int8.onnx",
        "https://huggingface.co/ogkalu/comic-text-and-bubble-detector/resolve/16e8a622f91fabc6b5b65c96d32d1183f8843546/detector_int8.onnx",
        "b5022ad46416b6fe4f88b0cc082cfd2ff5b1cfc624088c2f19879485493f5913", 43838857, "Apache-2.0",
    )
    val OCR_DETECTOR = ModelFile(
        "ocr/PP-OCRv5_mobile_det.onnx",
        "https://huggingface.co/PaddlePaddle/PP-OCRv5_mobile_det_onnx/resolve/e6f4fa85f00e168c862bc462aebca69eef9b3d3d/inference.onnx",
        "a431985659dc921974177a95adcfbb90fd9e51989a5e04d70d0b75f597b6e61d", 4826518, "Apache-2.0",
    )
    val OCR_RECOGNIZER = ModelFile(
        "ocr/eslav_PP-OCRv5_rec_mobile_infer.onnx",
        "https://huggingface.co/ogkalu/ppocr-v5-onnx/resolve/3b76726c3606c4af92f5133b359ed14e0d30a455/eslav_PP-OCRv5_rec_mobile_infer.onnx",
        "08705d6721849b1347d26187f15a5e362c431963a2a62bfff4feac578c489aab", 7911802, "Apache-2.0",
    )
    val OCR_DICTIONARY = ModelFile(
        "ocr/ppocrv5_eslav_dict.txt",
        "https://huggingface.co/ogkalu/ppocr-v5-onnx/resolve/3b76726c3606c4af92f5133b359ed14e0d30a455/ppocrv5_eslav_dict.txt",
        "3e95f1581557162870cacdba5af91a4c6be2890710d395b0c3c7578e7ee5e6eb", 1663, "Apache-2.0",
    )
    val TTS_DURATION_PREDICTOR = ModelFile(
        "supertonic-3/onnx/duration_predictor.onnx",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/onnx/duration_predictor.onnx",
        "c3eb91414d5ff8a7a239b7fe9e34e7e2bf8a8140d8375ffb14718b1c639325db", 3700147, "OpenRAIL-M",
    )
    val TTS_TEXT_ENCODER = ModelFile(
        "supertonic-3/onnx/text_encoder.onnx",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/onnx/text_encoder.onnx",
        "c7befd5ea8c3119769e8a6c1486c4edc6a3bc8365c67621c881bbb774b9902ff", 36416150, "OpenRAIL-M",
    )
    val TTS_VECTOR_ESTIMATOR = ModelFile(
        "supertonic-3/onnx/vector_estimator.onnx",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/onnx/vector_estimator.onnx",
        "883ac868ea0275ef0e991524dc64f16b3c0376efd7c320af6b53f5b780d7c61c", 256534781, "OpenRAIL-M",
    )
    val TTS_VOCODER = ModelFile(
        "supertonic-3/onnx/vocoder.onnx",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/onnx/vocoder.onnx",
        "085de76dd8e8d5836d6ca66826601f615939218f90e519f70ee8a36ed2a4c4ba", 101424195, "OpenRAIL-M",
    )
    val TTS_CONFIG = ModelFile(
        "supertonic-3/onnx/tts.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/onnx/tts.json",
        "42078d3aef1cd43ab43021f3c54f47d2d75ceb4e75f627f118890128b06a0d09", 8253, "OpenRAIL-M",
    )
    val TTS_INDEXER = ModelFile(
        "supertonic-3/onnx/unicode_indexer.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/onnx/unicode_indexer.json",
        "9bf7346e43883a81f8645c81224f786d43c5b57f3641f6e7671a7d6c493cb24f", 277676, "OpenRAIL-M",
    )
    val TTS_VOICE_F1 = ModelFile(
        "supertonic-3/voice_styles/F1.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/F1.json",
        "bbdec6ee00231c2c742ad05483df5334cab3b52fda3ba38e6a07059c4563dbc2", 292046, "OpenRAIL-M",
    )
    val TTS_VOICE_F2 = ModelFile(
        "supertonic-3/voice_styles/F2.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/F2.json",
        "7c722c6a72707b1a77f035d67f0d1351ba187738e06f7683e8c72b1df3477fc6", 292423, "OpenRAIL-M",
    )
    val TTS_VOICE_F3 = ModelFile(
        "supertonic-3/voice_styles/F3.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/F3.json",
        "12f6ef2573baa2defa1128069cb59f203e3ab67c92af77b42df8a0e3a2f7c6ab", 290794, "OpenRAIL-M",
    )
    val TTS_VOICE_F4 = ModelFile(
        "supertonic-3/voice_styles/F4.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/F4.json",
        "c2fa764c1225a76dfc3e2c73e8aa4f70d9ee48793860eb34c295fff01c2e032b", 291808, "OpenRAIL-M",
    )
    val TTS_VOICE_F5 = ModelFile(
        "supertonic-3/voice_styles/F5.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/F5.json",
        "45966e73316415626cf41a7d1c6f3b4c70dbc1ba2bee5c1978ef0ce33244fc8d", 291479, "OpenRAIL-M",
    )
    val TTS_VOICE_M1 = ModelFile(
        "supertonic-3/voice_styles/M1.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/M1.json",
        "e35604687f5d23694b8e91593a93eec0e4eca6c0b02bb8ed69139ab2ea6b0a5b", 291748, "OpenRAIL-M",
    )
    val TTS_VOICE_M2 = ModelFile(
        "supertonic-3/voice_styles/M2.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/M2.json",
        "b76cbf62bac707c710cf0ae5aba5e31eea1a6339a9734bfae33ab98499534a50", 292055, "OpenRAIL-M",
    )
    val TTS_VOICE_M3 = ModelFile(
        "supertonic-3/voice_styles/M3.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/M3.json",
        "ea1ac35ccb91b0d7ecad533a2fbd0eec10c91513d8951e3b25fbba99954e159b", 290198, "OpenRAIL-M",
    )
    val TTS_VOICE_M4 = ModelFile(
        "supertonic-3/voice_styles/M4.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/M4.json",
        "ca8eefad4fcd989c9379032ff3e50738adc547eeb5e221b82593a6d7b3bac303", 291522, "OpenRAIL-M",
    )
    val TTS_VOICE_M5 = ModelFile(
        "supertonic-3/voice_styles/M5.json",
        "https://huggingface.co/Supertone/supertonic-3/resolve/3cadd1ee6394adea1bd021217a0e650ede09a323/voice_styles/M5.json",
        "dd22b92740314321f8ae11c5e87f8dd60d060f15dd3a632b5adf77f471f77af2", 291469, "OpenRAIL-M",
    )

    val TTS_VOICES: Map<String, ModelFile> = mapOf(
        "F1" to TTS_VOICE_F1,
        "F2" to TTS_VOICE_F2,
        "F3" to TTS_VOICE_F3,
        "F4" to TTS_VOICE_F4,
        "F5" to TTS_VOICE_F5,
        "M1" to TTS_VOICE_M1,
        "M2" to TTS_VOICE_M2,
        "M3" to TTS_VOICE_M3,
        "M4" to TTS_VOICE_M4,
        "M5" to TTS_VOICE_M5,
    )

    val ALL: List<ModelFile> = listOf(
        DETECTOR, OCR_DETECTOR, OCR_RECOGNIZER, OCR_DICTIONARY, TTS_DURATION_PREDICTOR, TTS_TEXT_ENCODER, TTS_VECTOR_ESTIMATOR, TTS_VOCODER, TTS_CONFIG, TTS_INDEXER,
    ) + TTS_VOICES.values
}
