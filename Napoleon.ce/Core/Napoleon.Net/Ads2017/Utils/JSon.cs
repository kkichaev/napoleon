using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Reflection;
using System.Text;

namespace Ads2017
{
    class JSon
    {
        public string Serialize(object obj)
        {
            StringBuilder sb = new StringBuilder();
            DoSerialize(sb, obj);
            return sb.ToString();
        }

        private void DoSerialize(StringBuilder sb, object obj)
        {
            if (IsList(obj))
                DoSerializeList(sb, obj);
            else
                DoSerializeObject(sb, obj);
        }

        public static long ToUnixTimne(DateTime date)
        {
            DateTime UnixEpoch = new DateTime(1970, 1, 1, 0, 0, 0, 0, DateTimeKind.Utc);
            return (long)((date - UnixEpoch).TotalSeconds * 1000);
        }

        private void DoSerializeObject(StringBuilder sb, object obj)
        {
            if (obj is DateTime)
            {
                DateTime dt = (DateTime)obj;
                sb.Append(ToUnixTimne(dt));
            }
            else
            {
                sb.Append("{");
                SeriazableFields(sb, obj);
                sb.Append("}");
            }
        }

        private void SeriazableFields(StringBuilder sb, object obj)
        {
            StringBuilder res = new StringBuilder();

            FieldInfo[] fi = obj.GetType().GetFields(BindingFlags.Instance | BindingFlags.Public);

            foreach (FieldInfo f in fi)
            {
                if (res.Length > 0)
                    res.Append(", ");

                SeriazableField(res, f.Name, f.GetValue(obj));
            }

            sb.Append(res.ToString());
        }

        private void SeriazableField(StringBuilder sb, string name, object obj)
        {
            sb.Append("\"").Append(name).Append("\"");
            sb.Append(" : ");

            if (obj != null)
            {
                if (IsList(obj))
                    DoSerializeList(sb, obj);
                else if (IsBoolean(obj))
                    SerializeString(sb, obj.ToString().ToUpper());
                else if (obj.GetType().IsPrimitive)
                    sb.Append(obj.ToString().Replace(',', '.'));
                else if (IsString(obj))
                    SerializeString(sb, obj);
                else
                    DoSerializeObject(sb, obj);
            }
            else
                SerializeString(sb, "NULL");

        }

        private bool IsBoolean(object o)
        {
            return o.GetType() == typeof(Boolean) || o.GetType() == typeof(bool);
        }

        private bool IsString(object o)
        {
            return o.GetType() == typeof(String) || o.GetType() == typeof(string);
        }


        private void SerializeString(StringBuilder sb, object o)
        {
            sb.Append("\"");
            string s = StringUtil.HtmlQuotes(o.ToString());
            s = EscapeNewLine(s);
            sb.Append(s);
            sb.Append("\"");
        }

        private string EscapeNewLine(string s)
        {
            return s.Replace("\n", "\\\n");
        }

        private void DoSerializeList(StringBuilder sb, object obj)
        {
            sb.Append("[");
            StringBuilder res = new StringBuilder();

            foreach (object o in (IEnumerable)obj)
            {
                if (res.Length > 0)
                    res.Append(", ");

                DoSerialize(res, o);
            }

            sb.Append(res.ToString());
            sb.Append("]");
        }

        public bool IsList(object o)
        {
            bool result = false;

            if (o != null)
                result = o is IList && o.GetType().IsGenericType &&
                   (o.GetType().GetGenericTypeDefinition().IsAssignableFrom(typeof(List<>)) ||
                   o.GetType().GetGenericTypeDefinition().IsAssignableFrom(typeof(ObservableCollection<>)));

            return result;
        }
    }
}
