if (OBJECT_ID('Org', 'U') is null)
create table Org (id NVARCHAR(300) primary key, name NVARCHAR(300), [address] NVARCHAR(1000), costype int)

if (OBJECT_ID('OrgContacts', 'U') is null)
create table OrgContacts (ido NVARCHAR(300) FOREIGN KEY REFERENCES Org(id), 
id NVARCHAR(300), name NVARCHAR(300), phone NVARCHAR(300))

if (OBJECT_ID('OrgTypeCost', 'U') is null)
create table OrgTypeCost (ido NVARCHAR(300) FOREIGN KEY REFERENCES Org(id), [type] int, fid NVARCHAR(300))

if (OBJECT_ID('OrgDogovors', 'U') is null)
create table OrgDogovors (ido NVARCHAR(300) FOREIGN KEY REFERENCES Org(id), 
id NVARCHAR(300), name NVARCHAR(300), firm int)

if (OBJECT_ID('AgentOrgs', 'U') is null)
create table AgentOrgs (ido NVARCHAR(300) FOREIGN KEY REFERENCES Org(id), 
userid NVARCHAR(20) FOREIGN KEY REFERENCES Agents(id))


if (OBJECT_ID('AddOrg', 'P') IS NULL) 
 exec('
 create procedure AddOrg(@id NVARCHAR(300), @name NVARCHAR(300), @address NVARCHAR(1000), @costype int)
as 
	if exists(select * from [Org] where [id] = @id )
		update [Org] set [name] = @name, [address] = @address, costype = @costype where [id] = @id
	else
		insert into Org ([id], [name], [address], [costype]) values (@id, @name, @address, @costype)
')

if (OBJECT_ID('PriceCost', 'U') is null)
create table PriceCost (id VARCHAR(20), [type] int, cost real)

if (OBJECT_ID('PriceQty', 'U') is null)
create table PriceQty (id VARCHAR(20), [type] int, qty real)
