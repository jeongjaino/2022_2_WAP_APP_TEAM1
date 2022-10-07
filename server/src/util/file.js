'use strict';

const { QueryTypes } = require("sequelize");
const crypto = require('crypto');
const fs = require('fs');
const path = require('path');
const db = require('../config-db');
module.exports = async function(buffer, originalfilename) {
    let sha256 = crypto.createHash("sha256").update(buffer).digest("hex");
    let md5 = crypto.createHash("md5").update(buffer).digest("hex");
    let filename = sha256 + md5 + path.extname(originalfilename);
    let pool = db.getDb();
    try {
        pool.authenticate();
    } catch {
        return {result: false, fileName: ""};
    }
    try {
        let response = await pool.query(`SELECT * FROM DB_FILE_INFO WHERE SHA256 = '${sha256}' and MD5 = '${md5}'`, { type: QueryTypes.SELECT });
        if (response.length != 0) {
            return {result: true, fileName: filename};
        }
        response = await pool.query(`INSERT INTO DB_FILE_INFO (FILENAME, SHA256, MD5) VALUES ('${filename}', '${sha256}', '${md5}')`, { type: QueryTypes.INSERT });
        fs.writeFileSync(path.join(__dirname, '../../data', filename), buffer);
        return {result: true, fileName: filename};
    } catch (err) {
        console.log('error ', err);
        return {result: false, fileName: ""};
    }

}