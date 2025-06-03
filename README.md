Descripción del Proyecto: FacturaMaven
FacturaMaven es una aplicación de consola desarrollada en Java que automatiza el proceso de lectura, clasificación y extracción de información clave desde facturas electrónicas recibidas por correo electrónico. El sistema está orientado al procesamiento de facturas emitidas en Costa Rica según el formato XML oficial del Ministerio de Hacienda, así como documentos en PDF e imagen (PNG/JPG).

🔧 Funcionalidades principales:
📬 Conexión segura a Gmail: uso de contraseña de aplicación para acceder y filtrar correos con facturas.

📥 Descarga automática de archivos adjuntos (PDF, XML, imágenes).

🧠 Análisis inteligente del contenido: reconocimiento de palabras clave como "SINPE" o "Transferencia".

📄 Extracción de datos desde XML: número de consecutivo, líneas de detalle (código, cantidad, descripción, IVA, total).

🔍 OCR para PDF e imágenes: lectura de texto para detectar datos financieros importantes.

🗃️ Almacenamiento estructurado en SQL Server mediante JDBC y DAO.

✅ Validación de archivos XML: solo se procesan documentos con raíz <FacturaElectronica> o <TiqueteElectronico>.

🛠️ Tecnologías utilizadas:
Lenguaje: Java 17+

Framework de gestión: Maven

OCR: Tesseract (opcional según implementación)

Base de datos: SQL Server

Conexión a correo: JavaMail

IDE: IntelliJ IDEA

Control de versiones: Git
