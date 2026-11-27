# contracts

Единственное описание данных между приложением и сервером (JSON Schema, типы в `$defs`).

- Правишь только здесь, потом: `python3 tools/gen_contracts.py`.
- Результат: `app/shared/contracts_gen/` (Kotlin) и `server/src/shared/contracts_gen/` (Python).
  Эти папки руками не правятся; CI проверяет `python3 tools/gen_contracts.py --check`.
- Имена полей — `snake_case`. Ссылки на другой файл: `"common.schema.json#/$defs/Lang"`.

Генератор понимает: `object` + `properties` + `required`, `enum` строк, `string`, `integer`,
`number`, `boolean`, `array` + `items`, `$ref`. Поле не в `required` — необязательное.
