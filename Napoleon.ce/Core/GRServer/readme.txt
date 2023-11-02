1. Параметры по умолчанию в запросах (SQLQuery) и таблицах (SQTable). 
          <source name="SQLQuery">
            <param name="stmt">
              "select * from TransferAnswer where date &gt;= $01 and userid='$02'"
            </param>
            <param name="$01">ToDate(FormatDate(AdjustDate(Now(), -3, "month"),"DD.MM.YYYY"))</param>
            <param name="$02">$user.id</param>
            <param name="debug" />
          </source>
		  
   В клиенте перечисляем через ';' 
   'ToDate("01/10/2022");101' - для SQLQuery
   'PARAMS:ToDate("01/10/2022");101' - для SQTable


2. DBFShadowReader - для копирования объекта из DBF в SQL базу тип источника any, для удаления файла надо добавит параметр removeAfterReading
      <sources>
        <source name="DBFShadowReader" type="any"> 
          <param name="userTable">"TRA"</param>
          <param name="commonTable">"__TRA"</param>
          <param name="catalog">"number,date;items;1"</param>
          <param name="removeAfterReading" /> 
        </source>
        <source name="SQTable" type="internal" />
      </sources>

3. Можно указать полный запрос для таблицы. а не просто where этот запрос будет использоваться для выбора данных
        <source name="SQTable" type="internal">
          <param name="stmt">
            "select ta.* from TransferAnswer ta left join TransferCommit tc on
              ta.userid = tc.userid and ta.date = tc.date and ta.number = tc.number 
              where tc.userid is null and ta.userid='"+ $user.id + "'"
          </param>
        </source>

4. Query в python выбирает объект с подобъектами в описанни объектов @ - задает имя поля в запросе, для подобъектов указываются поля группировки если несколько, через запятую. Запрос должен быть отсортирован по этим полям
  stmt = """
  select o."name", oi."name" as name_i, oi.folder as folder, oi."id" as id_i, oi."qtyInPack", sum(oi."qty") as qty from 
   (select o."created", o."userid", o."id", org."name", o."date" from "Order" o 
      left join "Org" org on o."id" = org."id") o, 
   (select o."id", o."qty", p."qtyInPack", p."name", p.folder, o."Order$created" as created, o."Order$userid" as userid from "Order$items" o
      left join (select p."id", p."qtyInPack", p."name", f."name" as folder from "Price" p left join "Folder" f on p."fid" = f."fid") p
       on o."id" = p."id") oi 
    where o."userid" = oi."userid" and o."created" = oi."created" 
      and o."{0}" >= ToDate("{1} 0:0:0") and o."{0}" < ToDate("{2}") and o."userid" in ({3})
    group by folder, o."name", name_i, id_i, oi."qtyInPack"
    order by folder, name_i
  """.format(
      params.field,
      params.start.strftime('%d.%m.%Y'), 
      (params.finish + timedelta(days=1)).strftime('%d.%m.%Y'),
      unpack(params.userids))

  ord = server.Query(stmt, "Orders[name@name_i:s,id@id_i:s,folder:s,qtyInPack:n(3),orgs(name_i)[qty:n(3),name:s]]")
  # name_i - поле запроса
  #orgs - подобъект собирается по полю name_i
  
