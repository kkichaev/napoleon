<?xml version="1.0" ?>
<serverDefs xmlns="http://grsoft.ru/schemas/serverDefs.xsd">
   <objects>
      <object name="Agents">
         <sources>
            <source name="DBFTable">
               <param name="tableName">"USERS1"</param>
            </source>
         </sources>

         <member name="primaryKey">"id"</member>

         <field name="id" type="string" width="20" />
         <field name="name" type="string" width="50" />
         <field name="login" type="string" width="30" />
         <field name="password" type="string" width="30" />
      </object>

      <object name="Price">
         <sources>
            <!-- таблица со включенным фильтром qty > 0  выключать/включать SetFilter(False/True) -->
            <source name="DBFPriceTable">
               <param name="userTable">"W"</param>
               <param name="commonTable">"WAREHOUS"</param>
               <param name="filter">"[folder] in $folder.fid"</param>
            </source>
         </sources>

         <field name="id" type="string" />
         <field name="fid" type="string" data="folder" hidden="true" />
         <field name="folderID" type="number" execOn="Get">FolderID($object.fid)</field>
         <field name="qtyInPack" type="number" prec="3" data="inpack" />
         <field name="name" type="string" />
         <field name="qty" type="number" prec="3" />
         <field name="weight" type="number" prec="3" />
         <field name="color" type="hex" />
         <field name="tax1" type="number" prec="0" data="TAX1" />
         <field name="limit" type="number" prec="0" />
         <field name="cost" type="collection">
            <data>
               <object name="CostItem">
                  <sources>
                     <source name="Sequence">
                        <param name="source">$parent.source</param>
                     </source>
                  </sources>
                  <field name="cost" type="number" prec="2" />
               </object>
            </data>
         </field>
      </object>

      <object name="Order">
         <sources>
            <source name="DBFCatalogTable">
               <param name="tableName">$object.table + $user.id</param>
               <param name="catalog">"id"</param>
               <param name="ordered">1</param>
            </source>
            <source name="SQTable" type="internal" />
         </sources>

         <member name="table">"ORD"</member>
         <member name="primaryKey">"userid,created"</member>

         <field name="userid" type="string" data="USER_ID" execOn="Put">$user.id</field>
         <field name="created" type="timestamp" data="CREATED,NUM"/>
         <field name="sended" type="timestamp" data="S_DATE" execOn="Put">Now()</field>
         <field name="date" type="date" />
         <field name="delay" type="number" prec="0" />
         <field name="paydate" type="date" execOn="Put">AdjustDate($object.date, $object.delay, "day")</field>
         <field name="id" width="20" type="string" />
         <field name="remark" type="string" />
         <field name="items" type="collection">
            <data>
               <object name="OrderItem">
                  <sources>
                     <source name="DBFCatalogItemTable"/>
                     <source name="SQTable" type="internal" />
                  </sources>

                  <field name="id" width="20" type="string" data="ID_I"/>
                  <field name="qty" type="number" prec="3" />
                  <field name="cost" type="number" prec="2" />
                  <field name="discount" type="number" prec="1" />
                  <field name="ordflag" width="1" type="number" execOn="Put">FirstItem($object)</field>
               </object>
            </data>
         </field>

         <field name="ctype" type="string" width="30" execOn="Put">KeyValueReader("order.cfg","ВидЦены",$object.sumType)</field>
         <field name="firma" type="string" width="30" execOn="Put">KeyValueReader("order.cfg","Организация",$object.supplyer)</field>

         <field name="latitude" type="number" prec="5" />
         <field name="longitude" type="number" prec="5" />

         <field name="survay" type="collection">
            <data>
               <object name="Survay">
                  <sources>
                     <source name="DBFTable">
                        <param name="tableName">"OF" + $user.id</param>
                     </source>
                     <source name="SQTable" type="internal" />
                  </sources>

                  <field name="fid" type="string" width="20" data="FOLDER" />
                  <field name="choice" type="string" width="50" />
                  <field name="id" type="string" width="20">$parent.id</field>
                  <field name="date" type="timestamp" width="20">$parent.created</field>
                  <field name="ordflag" width="1" type="number" execOn="Put">FirstItem($object)</field>
               </object>
            </data>
         </field>

         <events>
            <event type="put">
               <!--<action name="WriteOrder" /> !-->
               <action name="RunBat" />
               <action name="WriteInternal">$object.type</action>
               <action name="WriteToLog">$object.type;$object.created</action>
            </event>
         </events>
      </object>
   </objects>
</serverDefs>
