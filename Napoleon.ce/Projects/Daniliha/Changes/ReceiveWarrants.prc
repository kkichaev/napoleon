create or replace PROCEDURE RECEIVEWARRANTS AS 
cursor docs is
  select o.* from "MoneyProxy" o left join "OrderCommitted" oc
    on o."created" = oc."created" and o."userid" = oc."userid" and oc."type" = 'MoneyProxy'
     where oc."created" is null and
       o."created" > (((sysdate - to_date('01011970','DDMMYYYY')) * (86400) - 3 * 86400 + 11644473600) * 10000000);

r_doc docs%rowtype;
crn number;
company number;
jur_pers number;
doc_type number;
doc_pref parus.consumerord.ord_pref%type;
supplyer number;
payer number;
company_agent number;
receip number;
doc_date date;
issue_date date;
expir_date date;
doc_sum real;

doc_rn number;
doc_number parus.consumerord.ord_numb%type;
doc1_rn number;

nomen number;

BEGIN
  open docs;
  
  select rn into crn from parus.acatalog where docname = 'WarrantMaterialValues' and is_root = 1;
  select rn, agent into company, company_agent from parus.companies where rownum < 2;
  select rn into doc_type from parus.doctypes where doccode = 'ДОВ с чек';  
  select rn into jur_pers from parus.jurpersons where main_sign = 1;
  select rn into payer from parus.agnacc where agnrn = company_agent;
  
  select rn into nomen from PARUS.dicnomns where nomen_code = 'Денежные средства';
  
  loop
    fetch docs into r_doc;
    exit when docs%notfound;
    
    select parus.seq_rn.nextval into doc_rn from dual;
    select parus.seq_rn.nextval into doc1_rn from dual;
    
    doc_date := current_date;
    doc_pref := to_char(extract(year from doc_date));
    doc_sum := r_doc."sum";
    issue_date := to_date('01011970','ddmmyyyy') + numtodsinterval(r_doc."date" / 10000000 - 11644473600, 'SECOND');
    expir_date := issue_date + numtodsinterval(5, 'DAY');
    
    supplyer := r_doc."id";
    receip := r_doc."userid";
    
    PARUS.p_warrants_next_numb(company, 'ДОВ с чек', doc_pref, doc_number);
    
    insert into PARUS.warrants
    (RN,COMPANY,CRN,JUR_PERS,DOC_TYPE,DOC_PREF,DOC_NUMB,DOC_DATE,ISSUE_DATE,EXPIR_DATE,"STATE",STATE_DATE,RECIPIENT,SUPPLIER,PAYER_ATTR,NOTE)
    values
    (doc_rn,company,crn,jur_pers,doc_type,doc_pref,doc_number,doc_date,issue_date,expir_date,0,doc_date,receip,supplyer,payer,r_doc."remark");
    
    insert into PARUS.warrant_spec
    (RN,PRN,COMPANY,CRN,NOMEN,MEAS_MAIN)
    values
    (doc1_rn, doc_rn, company, crn, nomen, r_doc."sum");
    
    insert into "OrderProceeded" ("created", "userid", "type", "remark")
          values (r_doc."created", r_doc."userid", 'MoneyProxy', 'Загружен' );
    insert into "OrderCommitted" ("created", "number", "userid", "type")
          values (r_doc."created", to_char(doc_rn), r_doc."userid", 'MoneyProxy' );
    
    commit;
  end loop;
  close docs;

END RECEIVEWARRANTS;