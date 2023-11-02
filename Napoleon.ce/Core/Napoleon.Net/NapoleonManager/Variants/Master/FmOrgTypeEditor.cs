using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   public partial class FmOrgTypeEditor : Form
   {
      private DataSet<string, Org> dsOrg;
      private DataSet<string, OrgType> dsOrgType;
      private DataSet<string, OrgMem> dsOrgMem;

      public FmOrgTypeEditor()
      {
         InitializeComponent();
         grid.AutoGenerateColumns = false;
         btnSave.Enabled = false;
         dsOrg = (DataSet<string, Org>)DataModule.Get(Org.COMMON_OBJECT_NAME) ?? new DataSet<string, Org>(Org.COMMON_OBJECT_NAME);
         dsOrgType = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ?? new DataSet<string, OrgType>(OrgType.OBJECT_NAME);
         dsOrgMem = (DataSet<string, OrgMem>)DataModule.Get(OrgMem.OBJECT_NAME) ?? new DataSet<string, OrgMem>(OrgMem.OBJECT_NAME);
      }

      private void btnRefresh_Click(object sender, EventArgs e)
      {
         List<IDataSet> upd = new List<IDataSet>();
         upd.Add(dsOrg);
         upd.Add(dsOrgType);
         upd.Add(dsOrgMem);
         FmWait.StdDataRefresh(this, upd, DoLoadData);
      }

      private void DoLoadData() 
      {
         LoadItems();
         LoadData();
      }

      private void LoadData()
      {
         List<TypeData> data = new List<TypeData>();
         LoadDataOrg(data);
         data.Sort((lhs, rhs) => { return lhs.name.CompareTo(rhs.name); });
         grid.DataSource = data;
      }

      private void LoadDataOrg(List<TypeData> data)
      {
         foreach (Org o in dsOrg.Data)
            data.Add(CreateDataType(o));
      }

      private TypeData CreateDataType(Org o)
      {
         TypeData od = new TypeData();
         od.id = o.id;
         od.address = o.address;
         od.name = o.name;

         if (dsOrgMem.ContainsKey(o.id))
            od.type = dsOrgMem[o.id].type;

         return od;
      }

      private void LoadItems()
      {
         List<OrgType> items = new List<OrgType>();
         items.AddRange(dsOrgType.Values);
         items.Sort((lhs, rhs) => { return lhs.Name.CompareTo(rhs.Name); });
         items.Insert(0, new OrgType());

         clmnTypes.ValueMember = "ID";
         clmnTypes.DisplayMember = "Name";
         clmnTypes.DataSource = items;
      }

      private void btnOrgType_Click(object sender, EventArgs e)
      {
         new FmOrgType().Show();
      }

      private void btnSave_Click(object sender, EventArgs e)
      {
         CollectData();

         List<IDataSet> wrSet = new List<IDataSet>();
         wrSet.Add(dsOrgMem);

         if (!DataModule.UpdateDataSet(wrSet, null, null, Config.GetConfig().GetConnection()))
            DialogUtil.UpdateErrMsg(this);
         else
            btnSave.Enabled = false;
      }

      private void CollectData()
      {
         foreach (TypeData t in (List<TypeData>)grid.DataSource)
         {
            if (dsOrgMem.ContainsKey(t.id))
               dsOrgMem[t.id].type = t.type;
            else
            {
               OrgMem om = new OrgMem();
               om.id = t.id;
               om.type = t.type;
               dsOrgMem.Add(om.id, om);
            }
         }
      }

      private void grid_CellEndEdit(object sender, DataGridViewCellEventArgs e)
      {
         btnSave.Enabled = true;
      }

      private void FmOrgTypeEditor_FormClosing(object sender, FormClosingEventArgs e)
      {
         if (btnSave.Enabled && DialogUtil.AskToSave(this))
            btnSave.PerformClick();
      }
   }

   public class TypeData
   {
      public string id = string.Empty;
      public string name = string.Empty;
      public string address = string.Empty;
      public string type = string.Empty;

      private static OrgType emtyOrg = new OrgType();

      public string Name { get { return name; } }
      public string Address { get { return address; } }
      public string Type { get { return type; } set { type = value; } }
   }
}
