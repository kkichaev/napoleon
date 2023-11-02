using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using GRSoft.Network;
using GRSoft.NapoleonManager.Utils;
using GRSoft.NapoleonManager.Properties;
using System.Reflection.Emit;
using System.Reflection;
using System.Collections;

namespace GRSoft.NapoleonManager
{
   public class UserFormEx : UserForm
   {
      private DataSet<int, Matrix> dsMatrix;
      List<DataGridViewCheckBoxColumn> matrixColumns = new List<DataGridViewCheckBoxColumn>();
      Type dataType;

      public UserFormEx(Divisions owner) :
         base(owner)
      {
         dsMatrix = dsMatrix = DataModule.Get(Matrix.OBJECT_NAME) == null ? new DataSet<int, Matrix>(Matrix.OBJECT_NAME, true) :
            (DataSet<int, Matrix>)DataModule.Get(Matrix.OBJECT_NAME);
         dgvOrgs.CurrentCellDirtyStateChanged += new EventHandler(dgvOrgs_CurrentCellDirtyStateChanged);
         userDetails.Controls.Remove(udMatrix);
         //udMatrix.Hide();
      }

      void dgvOrgs_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         string name = dgvOrgs.Columns[dgvOrgs.CurrentCell.ColumnIndex].Name;
         foreach (DataGridViewCheckBoxColumn c in matrixColumns)
         {
            if (c.Name == name)
            {
               dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
               break;
            }
         }
      }

      protected override void BeforeUpdateData(String id, List<IDataSet> updSets)
      {
         dsMatrix.Filter = DataUtils.USERID_IS_NULL_STR;
         updSets.Add(dsMatrix);
      }

      string ToPropName(string matrixName)
      {
         if (Char.IsDigit(matrixName[0]))
            matrixName = "D_ig" + matrixName;
         return matrixName.Replace(" ", "SpC");
      }

      string ToMatrixName(string propName)
      {
         return propName.Replace("D_ig", "").Replace("SpC", " ");
      }

      protected override void FillListOrgs()
      {
         List<KeyValuePair<int, Matrix>> removed = new List<KeyValuePair<int, Matrix>>();
         foreach (KeyValuePair<int, Matrix> kv in dsMatrix)
         {
            if (kv.Value.common != 0)
               removed.Add(kv);
         }
         removed.ForEach(x => dsMatrix.Remove(x.Key));

         if (dsMatrix.Count != matrixColumns.Count || dataType == null)
         {
            dataType = CreateOrgExType();

            dgvOrgs.SuspendLayout();

            foreach (DataGridViewCheckBoxColumn c in matrixColumns)
               dgvOrgs.Columns.Remove(c);
            matrixColumns.Clear();

            foreach (Matrix m in dsMatrix.Data)
            {
               DataGridViewCheckBoxColumn c = new DataGridViewCheckBoxColumn();
               c.Width = 30;
               c.DataPropertyName = ToPropName(m.name);
               matrixColumns.Add(c);
               dgvOrgs.Columns.Add(c);
            }

            dgvOrgs.ResumeLayout();
         }

         int i = 0;
         foreach(Matrix m in dsMatrix.Data)
         {
            matrixColumns[i].HeaderText = m.name;
            matrixColumns[i].Name = m.name;
            i++;
         }
         //for (int i = 0; i < dsMatrix.Count; i++)
         //{
         //   Matrix m = dsMatrix[i];
         //   matrixColumns[i].HeaderText = m.name;
         //   matrixColumns[i].Name = m.name;
         //}

         ArrayList orgs = new ArrayList();
         ConstructorInfo ci = dataType.GetConstructor(new Type[] { typeof(UserFormEx), typeof(Org) });
         foreach (Org o in dsOrg.Data)
         {
            object obj = ci.Invoke(new object[] { this, o });
            orgs.Add(obj);
         }

         orgs.Sort();
         dgvOrgs.DataSource = orgs;
      }

      public void SetMatrix(Org o, string name)
      {
         name = ToMatrixName(name);
         if (o.matrix.Count == 0)
            o.matrix.Add(new Org.OrgMatrix());

         Org.OrgMatrix om = o.matrix[0];
         if (om.name.CompareTo(name) != 0)
         {
            om.name = name;
            om.id = o.id;
            om.userid = Agent.id;
         }
         else
            o.matrix.Clear();

         owner.AddReplacedSet(Agent.id, GetMatrixSet());
         owner.AddReplacedSet(Agent.id, GetAgentMatrixSet());

         ArrayList dsrc = (ArrayList)dgvOrgs.DataSource;
         int index = 0;
         foreach (OrgEx oe in dsrc)
         {
            if (oe.Org == o)
            {
               dgvOrgs.InvalidateRow(index);
               break;
            }
            index++;
         }
         //dgvOrgs.Update();
      }

      private IDataSet GetAgentMatrixSet()
      {
         DataSet<int, AgentMatrix> ret = new DataSet<int, AgentMatrix>(AgentMatrix.OBJECT_NAME, false);
         foreach (Matrix m in dsMatrix.Data)
         {
            AgentMatrix am = new AgentMatrix();
            am.userid = Agent.id;
            am.name = m.name;

            ret.Add(ret.Count, am);
         }
         return ret;
      }

      private IDataSet GetMatrixSet()
      {
         DataSet<int, Org.OrgMatrix> ret = new DataSet<int, Org.OrgMatrix>(Org.OrgMatrix.OBJECT_NAME, false);
         foreach (Org o in dsOrg.Data)
         {
            if (o.matrix.Count > 0 && o.matrix[0].name.Length > 0)
            {
               Org.OrgMatrix om = o.matrix[0];
               om.id = o.id;
               ret.Add(ret.Count, om);
            }
         }
         return ret;
      }

      public bool GetMatrix(Org o, string name)
      {
         name = ToMatrixName(name);
         return (o.matrix.Count > 0) ? (o.matrix[0].name.CompareTo(name) == 0) : false;
      }

      Type CreateOrgExType()
      {
         Type retType = typeof(OrgEx);
         try
         {
            AssemblyBuilder assemblyBuilder = AppDomain.CurrentDomain.DefineDynamicAssembly(new AssemblyName("GRSoft.NapoleonManager"), AssemblyBuilderAccess.Run);
            ModuleBuilder moduleBuilder = assemblyBuilder.DefineDynamicModule("Dynamic.dll");
            TypeBuilder typeBuilder = moduleBuilder.DefineType("OrgExAdd");
            typeBuilder.SetParent(typeof(OrgEx));

            ConstructorBuilder cb = typeBuilder.DefineConstructor(MethodAttributes.Public, CallingConventions.Standard, new Type[] { typeof(UserFormEx), typeof(Org) });
            ILGenerator ilg = cb.GetILGenerator();
            ilg.Emit(OpCodes.Ldarg_0);
            ilg.Emit(OpCodes.Ldarg_1);
            ilg.Emit(OpCodes.Ldarg_2);
            ilg.Emit(OpCodes.Call, typeof(OrgEx).GetConstructor(new Type[] {typeof(UserFormEx), typeof(Org)}));
            ilg.Emit(OpCodes.Ret); 

            foreach (Matrix m in dsMatrix.Data)
            {
               string key = ToPropName(m.name);
               PropertyBuilder propertyBuilder = typeBuilder.DefineProperty(key, System.Reflection.PropertyAttributes.None, typeof(bool), Type.EmptyTypes);

               MethodBuilder getMethodBuilder = typeBuilder.DefineMethod("get_" + key, MethodAttributes.Public, CallingConventions.HasThis, typeof(bool), Type.EmptyTypes);
               ilg = getMethodBuilder.GetILGenerator();
               ilg.Emit(OpCodes.Ldarg_0);
               ilg.Emit(OpCodes.Ldstr, key);
               ilg.Emit(OpCodes.Call, typeof(OrgEx).GetMethod("Get"));
               ilg.Emit(OpCodes.Ret);
               propertyBuilder.SetGetMethod(getMethodBuilder);

               MethodBuilder setMethodBuilder = typeBuilder.DefineMethod("set_" + key, MethodAttributes.Public, CallingConventions.HasThis, typeof(void), new Type[] { typeof(bool) });
               ilg = setMethodBuilder.GetILGenerator();
               ilg.Emit(OpCodes.Ldarg_0);
               ilg.Emit(OpCodes.Ldstr, key);
               ilg.Emit(OpCodes.Call, typeof(OrgEx).GetMethod("Set"));
               ilg.Emit(OpCodes.Ret);
               propertyBuilder.SetSetMethod(setMethodBuilder);
            }

            retType = typeBuilder.CreateType();
         }
         catch (Exception)
         {
            MessageBox.Show("Ошибка в названиях матриц. Название может содержать буквы цифры и пробел.");
            retType = typeof(OrgEx);
         }

         return retType;
      }
   }

   public class OrgEx : IComparable
   {
      protected UserFormEx owner;
      protected Org o;

      public OrgEx(UserFormEx owner, Org o)
      {
         this.owner = owner;
         this.o = o;
      }

      public OrgEx()
      {
      }

      public string Name { get { return o.Name; } }

      public Org Org { get { return o; } }

      bool Test { get { return Get("test"); } set { Set("test"); } }

      public bool Get(string name)
      { 
         return owner.GetMatrix(o, name); 
      }
      
      public void Set(string name)
      { 
         owner.SetMatrix(o, name); 
      }


      #region Члены IComparable

      public int CompareTo(object obj)
      {
         return o.name.CompareTo(((OrgEx)obj).o.name);
      }

      #endregion
   }
}