import groovy.sql.Sql
import groovy.xml.MarkupBuilder
import static java.util.UUID.randomUUID

def sql = Sql.newInstance( 'jdbc:as400://robinnw;naming=system;libraries=MAIRIE,SIRH,SYSIBM;', 'opensirh', '***REMOVED***', 'com.ibm.as400.access.AS400JDBCDriver' )
def records = []
sql.eachRow( 'select * from siserv where (codact is null or codact = \'\') and (servi is not null and servi != \'\')') { records << it.toRowResult() }
def map = [:]

def filePath = 'export.xml'
def out = new OutputStreamWriter(new FileOutputStream(filePath),"UTF-8");
def id_noeud = 1
def id_service = 1
out.write('<?xml version="1.0" encoding="UTF-8" standalone="no"?>')
out.write(System.lineSeparator())
def xml = new MarkupBuilder(out)

xml.databaseChangeLog('xmlns':"http://www.liquibase.org/xml/ns/dbchangelog", 'xmlns:xsi':"http://www.w3.org/2001/XMLSchema-instance", 'xsi:schemaLocation':"http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.0.xsd") {
  
  changeSet(author:'rayni84', id:(randomUUID() as String)) {
    
    // INIT REVISION
    insert(tableName:'ADS_REVISION') {
        column(name:'ID_REVISION', value:1)
        column(name:'ID_AGENT', value:9005138)
        column(name:'DATE_MODIF', value:'2013-11-14 08:00:00')
        column(name:'DATE_EFFET', value:'2013-11-14 08:00:00')
        column(name:'DATE_DECRET', value:'2013-11-14 08:00:00')
        column(name:'DESCRIPTION', value:'Initialisation de l\'arbre des services.')
        column(name:'VERSION', value:0)
    }
    
    dropSequence(sequenceName:'ads_s_revision')
    createSequence(sequenceName:'ads_s_revision', incrementBy:1, minValue:0, startValue:2)
    addDefaultValue(tableName:'ads_revision', columnName:'id_revision', defaultValueSequenceNext:'ads_s_revision')
    
    // ROOT NODE
    insert(tableName:'ADS_NOEUD') {
    
        column(name:'ID_NOEUD', value:id_noeud++)
        column(name:'ID_SERVICE', value:id_service++)
        column(name:'SIGLE', value:'ROOT')
        column(name:'LABEL', value:'Noeud racine')
        column(name:'ID_REVISION', value:'1')
        column(name:'VERSION', value:0)
    }
    
    // VDN NODE
    insert(tableName:'ADS_NOEUD') {
    
        column(name:'ID_NOEUD', value:id_noeud++)
        column(name:'ID_NOEUD_PARENT', value:1)
        column(name:'ID_SERVICE', value:id_service++)
        column(name:'SIGLE', value:'VDN')
        column(name:'LABEL', value:'Ville de Noumea')
        column(name:'ID_REVISION', value:'1')
        column(name:'VERSION', value:0)
    }
    
    // FOR ALL NODES IN SISERV
    records.each {
        
        def servi = it
        println "$servi.servi -- ${servi.liserv} --"
        
        if (servi.servi.isNumber()) {
            println "could not take service because code is a number"
            return
        }
        
        // for all nodes without parent, set the Ville de Nouméa as parent
        def parentNode = (servi.sigle == servi.depend || servi.depend.trim() == "") ? 2 : map[servi.depend];
        
        if (!parentNode) {
            println "could not find parent node, skipping record"
            return;
        }
        
        map << [(servi.sigle):id_noeud++]
        
        insert(tableName:'ADS_NOEUD') {
            
            column(name:'ID_NOEUD', value:map[servi.sigle])
            column(name:'ID_NOEUD_PARENT', value:parentNode)
            column(name:'ID_SERVICE', value:id_service++)
            column(name:'SIGLE', value:servi.sigle.trim())
            column(name:'LABEL', value:servi.liserv.trim())
            column(name:'ID_REVISION', value:'1')
            column(name:'VERSION', value:0)
            
        }
        
        // ADD SISERV_INFO data
        if (servi.servi.trim() != "") {
            insert(tableName:'ADS_SISERV_INFO') {
                column(name:'ID_NOEUD', value:map[servi.sigle])
                column(name:'CODE_SERVI', value:servi.servi)
                column(name:'VERSION', value:0)
            }
        }
    }
    
    dropSequence(sequenceName:'ads_s_noeud')
    createSequence(sequenceName:'ads_s_noeud', incrementBy:1, minValue:0, startValue:id_noeud)
    addDefaultValue(tableName:'ads_noeud', columnName:'id_noeud', defaultValueSequenceNext:'ads_s_noeud')
    
    dropSequence(sequenceName:'ads_s_noeud_service')
    createSequence(sequenceName:'ads_s_noeud_service', incrementBy:1, minValue:0, startValue:id_service)
    addDefaultValue(tableName:'ads_noeud', columnName:'id_service', defaultValueSequenceNext:'ads_s_noeud_service')
    
  }
}

out.flush()
out.close()

println "Export done in ${filePath}"