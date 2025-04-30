#!/bin/bash

read -p "Nombre de usuario de MySQL: " DB_USER
read -s -p "Contraseña de MySQL (presiona Enter si no tienes): " DB_PASS
echo

DB_NAME="pdvpapeleria"
TABLES_SQL="tables.sql"
PROCEDURES_SQL="procedures.sql"

if [[ ! -f "$TABLES_SQL" ]]; then
  echo "Archivo '$TABLES_SQL' no encontrado."
  exit 1
fi

if [[ ! -f "$PROCEDURES_SQL" ]]; then
  echo "Archivo '$PROCEDURES_SQL' no encontrado."
  exit 1
fi

if [[ -z "$DB_PASS" ]]; then
  MYSQL="mysql -u $DB_USER"
else
  MYSQL="mysql -u $DB_USER -p$DB_PASS"
fi

EXISTS=$($MYSQL -e "SHOW DATABASES LIKE '$DB_NAME';" 2>/dev/null | grep "$DB_NAME")

if [[ -n "$EXISTS" ]]; then
  $MYSQL -e "DROP DATABASE $DB_NAME;"
fi

$MYSQL -e "CREATE DATABASE $DB_NAME CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" || {
  echo "Error al crear la base de datos."
  exit 1
  }
echo "Base de datos '$DB_NAME' creada."

echo "Ejecutando '$TABLES_SQL'..."
$MYSQL -u $DB_USER $DB_NAME < $TABLES_SQL

echo "Ejecutando '$PROCEDURES_SQL'..."
$MYSQL -u $DB_USER $DB_NAME < $PROCEDURES_SQL

echo "✔ Base de datos '$DB_NAME' inicializada correctamente."

