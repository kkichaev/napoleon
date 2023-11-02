using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Data.OleDb;
using System.Drawing;
using System.IO;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
    public partial class FmOrgLoad : Form
    {
        Agent agent;
        UserFormEx owner;

        public DataSet<string, OrgRegion> dsOrgRegion = new DataSet<string, OrgRegion>(OrgRegion.OBJECT_NAME);
        public DataSet<string, City> dsCity = new DataSet<string, City>(City.OBJECT_NAME);
        public DataSet<string, TypePTT> dsTypePTT = new DataSet<string, TypePTT>(TypePTT.OBJECT_NAME);
        public DataSet<string, SpecPTT> dsSpecPTT = new DataSet<string, SpecPTT>(SpecPTT.OBJECT_NAME);
        public DataSet<string, StaffPosition> dsStaffPosition = new DataSet<string, StaffPosition>(StaffPosition.OBJECT_NAME);

        public FmOrgLoad()
        {
            InitializeComponent();
        }

        public void SetData(Agent agent, UserFormEx owner)
        {
            this.agent = agent;
            this.owner = owner;
        }


        private void btnOpen_Click(object sender, EventArgs e)
        {
            if (openFileDialog1.ShowDialog() == DialogResult.OK)
            {
                tbPath.Text = openFileDialog1.FileName;
            }
        }

        private void bntLoad_Click(object sender, EventArgs e)
        {
            if (!File.Exists(tbPath.Text.Trim()))
            {
                MessageBox.Show(this, "Укажите путь к файлу Excel!", "Ошибка", MessageBoxButtons.OK, MessageBoxIcon.Error);
                tbPath.Focus();
                return;
            }

            List<IDataSet> upd = new List<IDataSet>();
            upd.Add(dsOrgRegion);
            upd.Add(dsCity);
            upd.Add(dsSpecPTT);
            upd.Add(dsTypePTT);
            upd.Add(dsStaffPosition);

            FmWait.StdDataRefresh(this, upd, DoLoadData);
        }

        private void DoLoadData()
        {
            Thread thread = new Thread(ExportData);
            thread.Start();
        }

        private void ExportData()
        {
            Dictionary<string, string> regionMap = new Dictionary<string, string>();
            Dictionary<string, string> cityMap = new Dictionary<string, string>();
            Dictionary<string, string> specPTTMap = new Dictionary<string, string>();
            Dictionary<string, string> typePTTMap = new Dictionary<string, string>();
            Dictionary<string, string> staffPositionMap = new Dictionary<string, string>();

            SimpleDataSet<AgentOrgs> dsAOrgs = new SimpleDataSet<AgentOrgs>(AgentOrgs.OBJECT_NAME, false);

            foreach (OrgRegion or in dsOrgRegion.Values)
                regionMap[or.name.ToUpper()] = or.id;

            foreach (City c in dsCity.Values)
                cityMap[c.name.ToUpper()] = c.id;

            foreach (SpecPTT s in dsSpecPTT.Values)
                specPTTMap[s.name.ToUpper()] = s.id;

            foreach (TypePTT t in dsTypePTT.Values)
                typePTTMap[t.name.ToUpper()] = t.id;

            foreach (StaffPosition p in dsStaffPosition.Values)
                staffPositionMap[p.name.ToUpper()] = p.id;

            DataSet<string, Org> dsOrgLoad = new DataSet<string, Org>(Org.OBJECT_NAME);

            var conStr = string.Format("Provider=Microsoft.ACE.OLEDB.12.0;Data Source={0};Extended Properties='Excel 12.0 Xml;HDR=YES';", tbPath.Text.Trim());

            var objConn = new OleDbConnection(conStr);
            objConn.Open();
            var dt = objConn.GetOleDbSchemaTable(OleDbSchemaGuid.Tables, null);

            if (dt == null)
            {
                return;
            }

            foreach (DataRow sh in dt.Rows)
            {
                try
                {
                    string group = sh["TABLE_NAME"].ToString().Trim();

                    if (group.Contains("FilterDatabase"))
                        continue;

                    var adapter = new System.Data.OleDb.OleDbDataAdapter("SELECT * FROM [" + group + "]", conStr);
                    var ds = new DataSet();
                    adapter.Fill(ds, group);
                    DataTable data = ds.Tables[group];

                    Invoke(new InvokeDelegate(
                      delegate
                      {
                          progressBar1.Value = 0;
                          progressBar1.Maximum = data.Rows.Count;
                      }));

                    foreach (DataRow row in data.Rows)
                    {

                        Invoke(new InvokeDelegate(
                        delegate
                        {
                            progressBar1.PerformStep();
                        }));

                        object[] r = row.ItemArray;

                        if (r[0].ToString().Length == 0)
                            break;

                        Org o = new Org();

                        o.id = r[0].ToString().Trim();
                        o.name = CompileName(r);
                        o.nameYur = r[1].ToString().Trim();
                        o.nameFakt = r[2].ToString().Trim();

                        AgentOrgs ao = new AgentOrgs();
                        ao.id = o.id;
                        ao.userid = agent.id;
                        dsAOrgs.Add(ao);

                        string reg = r[3].ToString().Trim();

                        if (reg.Length > 0)
                        {

                            if (!regionMap.ContainsKey(reg.ToUpper()))
                            {
                                OrgRegion orgReg = new OrgRegion();
                                orgReg.id = GRSoft.Network.DataObject.GenId();
                                orgReg.name = reg;
                                dsOrgRegion.Add(orgReg.id, orgReg);

                                regionMap[reg.ToUpper()] = orgReg.id;
                            }

                            o.regionID = regionMap[reg.ToUpper()];
                        }

                        string ct = r[4].ToString().Trim();

                        if (ct.Length > 0)
                        {
                            if (!cityMap.ContainsKey(ct.ToUpper()))
                            {
                                City city = new City();
                                city.id = GRSoft.Network.DataObject.GenId();
                                city.name = ct;
                                dsCity.Add(city.id, city);

                                cityMap[ct.ToUpper()] = city.id;
                            }

                            o.cityID = cityMap[ct.ToUpper()];
                        }

                        string address = r[3].ToString().Trim() + "," + r[4].ToString().Trim() + "," + r[5].ToString().Trim();
                        o.address = address;
                        o.phone = r[6].ToString().Trim();
                        o.web = r[7].ToString().Trim();

                        string tp = r[8].ToString().Trim();

                        if (tp.Length > 0)
                        {
                            if (!typePTTMap.ContainsKey(tp.ToUpper()))
                            {
                                TypePTT typePTT = new TypePTT();
                                typePTT.id = GRSoft.Network.DataObject.GenId();
                                typePTT.name = tp;
                                dsTypePTT.Add(typePTT.id, typePTT);

                                typePTTMap[tp.ToUpper()] = typePTT.id;
                            }

                            o.typepttID = typePTTMap[tp.ToUpper()];
                        }

                        string st = r[9].ToString().Trim();

                        if (st.Length > 0)
                        {
                            if (!specPTTMap.ContainsKey(st.ToUpper()))
                            {
                                SpecPTT specPTT = new SpecPTT();
                                specPTT.id = GRSoft.Network.DataObject.GenId();
                                specPTT.name = st;

                                dsSpecPTT.Add(specPTT.id, specPTT);

                                specPTTMap[st.ToUpper()] = specPTT.id;
                            }

                            o.specpttID = specPTTMap[st.ToUpper()];
                        }

                        o.avgSell = 0.0;
                        Double.TryParse(r[10].ToString().Trim(), out o.avgSell);

                        o.cordiantPart = 0.0;
                        Double.TryParse(r[11].ToString().Trim(), out o.cordiantPart);

                        o.faceAll = 0.0;
                        Double.TryParse(r[12].ToString().Trim(), out o.faceAll);

                        o.faceCoordiant = 0.0;
                        Double.TryParse(r[13].ToString().Trim(), out o.faceCoordiant);

                        o.contacts.Clear();

                        string cn = r[14].ToString().Trim();

                        if (cn.Length > 0)
                        {
                            OrgContact orgContact = new OrgContact();
                            orgContact.name = cn;

                            string ps = r[15].ToString().Trim();

                            if (ps.Length > 0)
                            {
                                if (!staffPositionMap.ContainsKey(ps.ToUpper()))
                                {
                                    StaffPosition staffPosition = new StaffPosition();
                                    staffPosition.id = GRSoft.Network.DataObject.GenId();
                                    staffPosition.name = ps;

                                    dsStaffPosition.Add(staffPosition.id, staffPosition);

                                    staffPositionMap[ps.ToUpper()] = staffPosition.id;
                                }

                                orgContact.staffPositionID = staffPositionMap[ps.ToUpper()];

                            }

                            orgContact.phone = r[16].ToString().Trim();

                            o.contacts.Add(orgContact);
                        }

                        dsOrgLoad.Add(o.id, o);
                    }
                }
                catch (Exception e)
                {
                    MessageBox.Show(e.ToString());
                }
            }
            objConn.Close();

            List<IDataSet> update = new List<IDataSet>();

            update.Add(dsOrgLoad);
            update.Add(dsOrgRegion);
            update.Add(dsCity);
            update.Add(dsTypePTT);
            update.Add(dsSpecPTT);
            update.Add(dsStaffPosition);

            ReplacedSet rs = new ReplacedSet(agent.id, dsAOrgs);
            List<ReplacedSet> rpl = new List<ReplacedSet>();
            rpl.Add(rs);

            if (update.Count > 0 || dsAOrgs.Count > 0)
            {
                Config cfg = Config.GetConfig();

                if (DataModule.UpdateDataSet(update, null, rpl, cfg.GetConnection()))
                {
                    Invoke(new InvokeDelegate(
                    delegate
                    {
                        MessageBox.Show("Данные по организациям загружены");
                        owner.OnLoadOrgs();
                        Close();
                    }));
                }
                else
                    Invoke(new InvokeDelegate(
                    delegate
                    {
                        MessageBox.Show("Ошибка записи в базу данных.", "Ошибка", MessageBoxButtons.OK,
                      MessageBoxIcon.Error);
                    }));
            }
            else
                Invoke(new InvokeDelegate(
                   delegate
                   {
                       MessageBox.Show("Данные уже загружены в систему, обновление не требуется.", "Информация", MessageBoxButtons.OK,
                      MessageBoxIcon.Information);
                   }));
        }

        private string CompileName(object[] r)
        {
           return String.Format("{0} ({1} / {2})", r[2].ToString().Trim(), r[1].ToString().Trim(), r[8].ToString().Trim());
        }
    }
}
