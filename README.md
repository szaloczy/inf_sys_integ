# Információs rendszerek integrálása

## Beadandó - monitoring system

### Feladat leírás 13. Nyomás figyelő rendszer (Pressure Monitoring System)
Készítsen egy alkalmazást, amely folyadék- vagy gáznyomás-adatokat figyel egy rendszerben. A feladat során három klienssel dolgozunk: egy nyomásgeneráló klienssel, egy nyomást figyelő processzorral és egy riasztást kiértékelő klienssel.

Komponens 1: Pressure Generation Client

Csatlakozás: A kliens a pressureQueue pontról-pontra típusú üzenetsorhoz csatlakozik.
Feladat: 4 másodpercenként küld véletlenszerű nyomásértékeket (pl. 0 és 10 bar között).
Komponens 2: Pressure Alert Processor

Üzenetfogadás: Kizárólag a pressureQueue üzeneteit kapja.
Feldolgozás: Meghatározza, hogy a nyomás értéke veszélyesen magas-e (például 8 bar felett).
Riasztás küldése: Ha 2 egymást követő mérés 8 bar felett van, akkor a processzor riasztást küld a pressureAlertQueue üzenetsorba: “High pressure alert: 2 consecutive readings above 8 bar detected.”
Komponens 3: Alert Reporting Client

Fogyasztás: A kliens olvassa a pressureAlertQueue üzeneteit.
Kimenet: Konzolra írja a riasztás szövegét, pl. “High pressure alert: 2 consecutive readings above 8 bar detected.”
Tesztek

Üzenetek küldése és fogadása: Ellenőrizze, hogy a nyomásértékek megfelelően jutnak el az első kliensből a processzorig.
Magas nyomás azonosítása: Tesztelje, hogy a processzor 8 bar felett helyesen detektálja-e a veszélyes értékeket.
Consecutive readings logika: Vizsgálja meg, hogy pontosan akkor jön-e létre riasztás, ha 2 egymást követő mérés magas.

### Python projekt - függőségek telepítése
```
python install -r requirements.txt
```

### Kliensek elindítás
```
- python alert_reporting_cleint.py
- python pressure_alert_processor.py
- python pressure_generation_client.py
```

### RabbitMQ szerver elindítás
```
docker compose up --build 
```

### Tesztek futtatása
```
python -m unittest discover -s tests -p "test_*.py" -v
```