CREATE OR REPLACE PROCEDURE ReceiveOrders
IS
cursor orders is
  select o.* from "Order" o left join "OrderCommitted" oc
    on o."created" = oc."created" and o."userid" = oc."userid" and oc."type" = 'Order'
     where oc."created" is null and
       o."created" > (((sysdate - to_date('01011970','DDMMYYYY')) * (86400) - 3 * 86400 + 11644473600) * 10000000);

r_order orders%rowtype;

crn number;
company number;
jur_pers number;
ord_doctype number;
ord_pref parus.consumerord.ord_pref%type;
faceacc number;
disp_type number;
pay_type number;
tarif number;
currency number;
ord_date date;
ordsum real;
pay_label varchar(30);
subdiv number;

doc_rn number;
doc_number parus.consumerord.ord_numb%type;
doc1_rn number;

item_rn number;
item_id number;
item1_rn number;
item_sum number;
nom_modif number;
nommon_pack number;
umeas_main number;
tax_group number;

pay_date date;

BEGIN
  open orders;

  select rn into crn from parus.acatalog where docname = 'ConsumersOrders' and is_root = 1;
  select rn into company from parus.companies where rownum < 2;
  select rn into ord_doctype from parus.doctypes where doccode = 'Заявка';
  select rn into currency from parus.curnames where curcode = 643;
  select rn into disp_type from parus.dicshpvw where rownum < 2;
  select rn into subdiv from PARUS.INS_DEPARTMENT where code = 'организация';

  select rn into tax_group from parus.dictaxgr where code = 'Без НДС';

  loop
    fetch orders into r_order;
    exit when orders%notfound;

--    DBMS_OUTPUT.PUT_LINE(r_order."id");

    select parus.seq_rn.nextval into doc_rn from dual;
    select parus.seq_rn.nextval into doc1_rn from dual;


    select rn into jur_pers from parus.jurpersons where main_sign = 1;
    ord_date := to_date('01011970','ddmmyyyy') + numtodsinterval(r_order."date" / 10000000 - 11644473600, 'SECOND');
    ord_pref := to_char(extract(year from ord_date));

    select rn, tarif into faceacc, tarif from parus.faceacc where agent = r_order."id" and executive != 76211/*Исключаем ответственного "Валеев"*/;

    if r_order."cash" > 0 then
      pay_label := 'Налично';
     else
      pay_label := 'Безналично';
     end if;
     select rn into pay_type from parus.azsgsmpaymentstypes where gsmpayments_mnemo = pay_label;

     select sum("cost" * "qty") into ordsum from "Order$items" oi where
            oi."Order$userid" = r_order."userid" and oi."Order$created" = r_order."created";

     parus.PKG_DOCUMENT.NEXT_NUMBER(COMPANY, 'CONSUMERORD', 'ORD_DOCTYPE', 'ORD_PREF', 'ORD_NUMB',
      'CONSUMERORDBUF', 'ORD_DOCTYPE', 'ORD_PREF', 'ORD_NUMB', ORD_DOCTYPE, ORD_PREF, doc_number);

     pay_date := to_date(current_date + 10);
     insert into parus.consumerord (rn, crn, company, subdiv, jur_pers, ord_doctype, ord_pref,
            agent, faceacc, ord_date, disp_type, pay_type, tarif, currency, acc_agent,
            state_date, release_date, price_date, pay_date, ord_numb,
            note,psumwtax,psumwotax)
       values( doc_rn, crn, company, subdiv, jur_pers, ord_doctype, ord_pref, r_order."id",
               faceacc, ord_date, disp_type,
               pay_type, tarif, currency, r_order."userid", to_date(current_date),
               pay_date, ord_date/*to_date(CURRENT_DATE)*/, pay_date,
               doc_number, r_order."remark", ordsum, ordsum
       );

       insert into PARUS.CONSUMERORDP(rn, prn, company, crn, perf_date, perf_numb)
       values (doc1_rn, doc_rn, company, crn, pay_date, '1');

--       DBMS_OUTPUT.PUT_LINE(doc_number || ' ' || doc_rn);

       for item in (
         select oi.* from "Order$items" oi
         where oi."Order$created" = r_order."created" and
         oi."Order$userid" = r_order."userid")
       loop
         select parus.seq_rn.nextval into item_rn from dual;
         select parus.seq_rn.nextval into item1_rn from dual;

         item_id := to_number(item."id");
         select rn into nom_modif from parus.nommodif where prn = item_id and rownum < 2;
         select umeas_main into umeas_main from parus.dicnomns where rn = item_id;
         nommon_pack := null;
         if item."pack" > 0 then
           select pk.rn into nommon_pack from parus.nomnmodifpack pk where pk.nomenpack in
                  (select rn from parus.nomnpack np 
                    where np.quant = (select min(quant) from parus.nomnpack np where quant >= 1 and np.prn = item_id)
                      and np.code != 'НЕ БРАТЬ'/* Исключаем упаковки с мнемокодом "НЕ БРАТЬ"*/
                      and np.prn =item_id);
         end if;

         item_sum := item."qty" * item."cost";

         insert into parus.consumerords (rn, prn, company, crn, nomen, nom_modif, nommod_pack,
                  umeas_main, tax_group, main_quant, exp_price, sumwtax, sumwotax) values
         (item_rn, doc_rn, company, crn, item_id, nom_modif, nommon_pack, umeas_main, tax_group,
         item."qty", item."cost", item_sum, item_sum);

         insert into parus.consumerordps(rn, prn, company, crn, perf, cs_date, perf_date, actpf_date, cust_date,
         exec_date, main_quant, actm_quant, execm_quant, custm_quant, sumwtax, sumwotax,
         actswtax,actswotax, execswtax,execswotax,custswtax,custswotax)
         values (item1_rn, item_rn, company, crn, doc1_rn, pay_date, pay_date, pay_date, pay_date, pay_date,
         item."qty",item."qty",item."qty",item."qty",item_sum,item_sum,
         item_sum,item_sum,item_sum,item_sum,item_sum,item_sum);

--         DBMS_OUTPUT.PUT_LINE(item_rn);
       end loop;

       insert into "OrderProceeded" ("created", "userid", "type")
              values (r_order."created", r_order."userid", 'Загружен' );
       insert into "OrderCommitted" ("created", "number", "userid", "type")
              values (r_order."created", to_char(doc_rn), r_order."userid", 'Order' );


       commit;
  end loop;

  close orders;

END;
/
