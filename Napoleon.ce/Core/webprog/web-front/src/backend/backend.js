import { api } from "boot/axios";
import { store } from "quasar/wrappers";
import { useMainStore } from "src/stores/main-store";

export async function registerUser(user) {
  console.log("registerUser");

  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  const url = "/api/users";

  const response = await api.post(url, user, {
    headers,
    withCredentials: true,
  });

  if (response.message != undefined)
    throw { message: "RegisterUser Error", code: response.message };

  return response;
}

export async function sendRegToken(token) {
  console.log("sendRegToken");
  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  const url = "/auth/cnf_email/" + token;

  const response = await api.get(url, {
    headers,
    withCredentials: true,
  });

  if (response.status != "200" || response.data == undefined)
    throw { message: "SendRegToken Error", code: response.status };

  return response;
}

export async function isAuth() {
  try {
    var res = await users();

    if (res) return true;
  } catch {
    return false;
  }

  return false;
}

export async function users() {
  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  const url = "/auth/user";

  var response = await api.get(url, {
    headers,
    withCredentials: true,
  });

  if (response.status != "200" || response.data == undefined)
    throw { message: "User Error", code: response.status };

  const store = useMainStore();
  store.user = response.data;

  return new Promise((resolve, reject) => {
    resolve(response.data);
  });
}

export async function loginUser(user) {
  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  const url = "/auth/login";

  const response = await api.post(
    url,
    { email: user.email, password: user.password, remember: user.remember },
    {
      headers,
      withCredentials: true,
    }
  );

  if (response.status != "200" || response.data == undefined)
    throw { message: "LoginUser Error", code: response.status };

  const store = useMainStore();
  store.user = response.data;

  return response;
}

export function zdig(dig) {
  if (dig < 10) dig = "0" + dig;

  return dig;
}

export async function registerProject(data) {
  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  var url = "/api/account";

  var response = await api.post(
    url,
    { country: data.country, currency: data.currency },
    {
      headers,
      withCredentials: true,
    }
  );

  if (response.status != "200" || response.data == undefined)
    throw { message: "Register Project Account Error", code: response.status };

  const now = new Date();
  const date = `${now.getFullYear()}${zdig(now.getMonth() + 1)}${zdig(
    now.getDate()
  )}`;
  const tarif = "standard";

  url = "/api/tarif";

  response = await api.post(
    url,
    { date: date, serverid: data.serverid, tarif: tarif },
    {
      headers,
      withCredentials: true,
    }
  );

  if (response.status != "200" || response.data == undefined)
    throw { message: "Register Project Tarif Error", code: response.status };

  return response;
}

export async function logout() {
  console.log("logout");
  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  const url = "/auth/logout";

  const response = await api.get(url, {
    headers,
    withCredentials: true,
  });

  const store = useMainStore();
  store.user = undefined;

  return response;
}

export async function getBallance(data) {
  console.log(`getBallance:  ${data.from} - ${data.to}`);
  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  const url = `/api/balance?start=${data.from}&finish=${data.to}`;
  const response = await api.get(url, {
    headers,
    withCredentials: true,
  });

  if (response.status != "200" || response.data == undefined)
    throw { message: "getBallance Error", code: response.status };

  for (var el of response.data)
    if (el.name == "Balance")
      return new Promise((resolve, reject) => {
        resolve(el.data[0]);
      });

  throw { message: "getBallance: Balance not found" };
}

export async function getServers() {
  console.log("getServers");
  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  const url = "/api/servers";

  const response = await api.get(url, {
    headers,
    withCredentials: true,
  });

  if (response.status != "200" || response.data == undefined)
    throw { message: "getServers Error", code: response.status };

  for (var el of response.data)
    if (el.name == "ServersList")
      return new Promise((resolve, reject) => {
        resolve(el.data);
      });

  throw { message: "getServers: ServersList not found" };
}

export async function getTarifs() {
  console.log("getTarifs");
  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  const url = "/api/tarifs";

  const response = await api.get(url, {
    headers,
    withCredentials: true,
  });

  if (response.status != "200" || response.data == undefined)
    throw { message: "getTarifs Error", code: response.status };

  for (var el of response.data)
    if (el.name == "Tarif")
      return new Promise((resolve, reject) => {
        resolve(el.data);
      });

  throw { message: "getTarifs: Tarif not found" };
}

export async function changeUser(data) {
  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
  };

  const url = "/auth/user";

  var response = await api.post(
    url,
    { name: data.name, surname: data.surname },
    {
      headers,
      withCredentials: true,
    }
  );

  if (response.status != "200" || response.data == undefined)
    throw { message: "User Error", code: response.status };

  const store = useMainStore();
  store.user = response.data;

  return new Promise((resolve, reject) => {
    resolve(response.data);
  });
}

export async function renameServer(code, name) {
  console.log("renameServer");

  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
    Authorization: "Bearer " + code,
  };

  const url = "/api/server";

  const response = await api.post(
    url,
    { name: name },
    {
      headers,
      withCredentials: true,
    }
  );

  if (response.status != "200")
    throw { message: "renameServer", code: response.message };

  return response;
}

export async function getObjects(code, name) {
  console.log("getObjects: ", name, " ", code);

  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
    Authorization: "Bearer " + code,
  };

  const url = "/grs/object/" + name;

  const response = await api.get(url, {
    headers,
    withCredentials: true,
  });

  if (response.status != "200" || response.data == undefined)
    throw { message: "getObjects", code: response.message };

  for (var el of response.data)
    if (el.name == name)
      return new Promise((resolve, reject) => {
        resolve(el.data);
      });

  throw { message: `getObjects: object: ${name} not found` };
}

export async function postObjects(code, name, data) {
  console.log("postObjects:" + name + " : " + data);

  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
    Authorization: "Bearer " + code,
  };

  const url = "/grs/object/";

  const response = await api.post(url, [{ name: name, data: data }], {
    headers,
    withCredentials: true,
  });

  if (response.status != "200" || response.data == undefined)
    throw { message: "postObjects", code: response.message };

  return response;
}

export async function deleteObjects(code, name, where) {
  console.log("deleteObjects: ", name, " where: ", where);

  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
    Authorization: "Bearer " + code,
  };

  const url = `/grs/object/${name}?${encodeURI(where)}`;

  const response = await api.delete(url, {
    headers,
    withCredentials: true,
  });

  if (response.status != "200")
    throw { message: "deleteObjects", code: response.message };

  return response;
}

export async function queryObjects(code, query) {
  console.log("queryObjects: ", query);

  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
    Authorization: "Bearer " + code,
  };

  const url = "/grs/query";
  const response = await api.post(url, query, {
    headers,
    withCredentials: true,
  });

  if (response.status != "200")
    throw { message: "queryObjects", code: response.message };

  return response;
}

export async function reqConnects(code) {
  console.log("reqConnects: ", code);

  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
    Authorization: "Bearer " + code,
  };

  const url = "/api/req_connect";
  const response = await api.get(url, {
    headers,
    withCredentials: true,
  });

  if (response.status != "200")
    throw { message: "reqConnects", code: response.message };

  for (var el of response.data)
    if (el.name == "ReqConnect")
      return new Promise((resolve, reject) => {
        resolve(el.data);
      });

  throw { message: "reqConnects: ReqConnect not found" };
}

export async function setConnects(code, link, val) {
  console.log("setConnects: ", code, val, link);

  const headers = {
    "Content-Type": "application/json;charset=UTF-8",
    Authorization: "Bearer " + code,
  }

  var url = "/api/req_connect";
  var response

  if (link){
    response = await api.post(url, val, {
      headers,
      withCredentials: true,
    });
  }else{
    response = await api.delete(url, {
      data: val,
      headers,
      withCredentials: true,
    });
  }

  if (response.status != "200")
    throw { message: "setConnects", code: response.message };

  return response
}
