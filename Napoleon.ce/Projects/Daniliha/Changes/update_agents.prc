create or replace procedure update_agents is
begin
  MERGE INTO GRSOFT."Agents" 
  USING (SELECT rn, AGNABBR as name FROM parus.agnlist where rn in 
        (select distinct executive from parus.faceacc where not executive is null)) s 
  ON ("id" = s.rn)
  WHEN MATCHED THEN UPDATE SET "name" = s.name
  WHEN NOT MATCHED THEN INSERT ("id", "name") VALUES (to_char(s.rn), s.name);
end update_agents;
/
