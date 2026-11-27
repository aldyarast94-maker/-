from shared.contracts_gen import BBox, Emotion, Lang, Line, LineType, Manner


def test_line_json_matches_app() -> None:
    """Те же имена полей и значения, что пишет приложение (см. ContractsJsonTest в app/shared)."""
    line = Line(id="1", bbox=BBox(x=0.1, y=0.2, w=0.3, h=0.4), text="Привет!", lang=Lang.RU,
                type=LineType.DIALOGUE, speaker_hint="девочка", emotion=Emotion.HAPPY,
                manner=Manner.SHOUT, intensity=0.8)
    data = line.model_dump(mode="json")
    assert data["speaker_hint"] == "девочка" and data["lang"] == "ru"
    assert Line.model_validate_json(line.model_dump_json()) == line
