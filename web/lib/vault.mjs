// Local encrypted workspace. Passphrases and keys never enter persistent storage.
const KEY="reqestra.web.v1";
const ITERATIONS=260000;
const encoder=new TextEncoder();
const decoder=new TextDecoder();

function toBase64(bytes) {
  let out="";
  for(let i=0;i<bytes.length;i+=8192)
    out+=String.fromCharCode(...bytes.subarray(i,i+8192));
  return btoa(out);
}
function fromBase64(text) {
  const binary=atob(text);
  return Uint8Array.from(binary,x=>x.charCodeAt(0));
}
async function derive(passphrase,salt) {
  const secret=await crypto.subtle.importKey("raw",encoder.encode(passphrase),"PBKDF2",false,["deriveKey"]);
  return crypto.subtle.deriveKey({
    name:"PBKDF2",salt,iterations:ITERATIONS,hash:"SHA-256",
  },secret,{name:"AES-GCM",length:256},false,["encrypt","decrypt"]);
}
function validateVault(data) {
  if(!data || data.format!=="reqestra-vault-v1"||!data.salt||!data.iv||!data.ciphertext||
    data.iterations!==ITERATIONS) throw Error("Invalid encrypted Reqestra vault.");
  return data;
}
export function hasVault() { return !!localStorage.getItem(KEY); }
export function getEncryptedBackup() {
  const raw=localStorage.getItem(KEY);
  if(!raw)throw Error("No hay archivo cifrado.");
  validateVault(JSON.parse(raw));
  return raw;
}
export function importEncryptedBackup(raw) {
  if(raw.length>8*1024*1024)throw Error("Respaldo demasiado grande.");
  validateVault(JSON.parse(raw));
  localStorage.setItem(KEY,raw);
}
export function deleteVault() { localStorage.removeItem(KEY); }
export async function createVault(passphrase,workspaceData) {
  if(typeof passphrase!=="string"||passphrase.length<10)
    throw Error("Vault passphrase must be at least 10 characters.");
  if(hasVault())throw Error("A vault already exists; unlock or export it before replacing it.");
  const salt=crypto.getRandomValues(new Uint8Array(16));
  const key=await derive(passphrase,salt);
  const wrapper={format:"reqestra-vault-v1",iterations:ITERATIONS,salt:toBase64(salt),iv:"",ciphertext:""};
  await saveWithWrapper(key,workspaceData,wrapper);
  return key;
}
async function saveWithWrapper(key,data,wrapper) {
  const iv=crypto.getRandomValues(new Uint8Array(12));
  const bytes=encoder.encode(JSON.stringify(data));
  if(bytes.length>5*1024*1024)throw Error("Espacio de trabajo demasiado grande.");
  const ciphertext=await crypto.subtle.encrypt({name:"AES-GCM",iv},key,bytes);
  wrapper.iv=toBase64(iv);
  wrapper.ciphertext=toBase64(new Uint8Array(ciphertext));
  localStorage.setItem(KEY,JSON.stringify(wrapper));
}
export async function saveVault(key,data) {
  if(!key)throw Error("Debes desbloquear el espacio cifrado.");
  const raw=localStorage.getItem(KEY);
  if(!raw)throw Error("No hay espacio cifrado.");
  await saveWithWrapper(key,data,validateVault(JSON.parse(raw)));
}
export async function unlockVault(passphrase) {
  const wrapper=validateVault(JSON.parse(localStorage.getItem(KEY)||"null"));
  const key=await derive(passphrase,fromBase64(wrapper.salt));
  let bytes;
  try {
    bytes=await crypto.subtle.decrypt({name:"AES-GCM",iv:fromBase64(wrapper.iv)},key,fromBase64(wrapper.ciphertext));
  } catch {
    throw Error("Wrong passphrase or damaged encrypted data.");
  }
  const data=JSON.parse(decoder.decode(bytes));
  if(!data || typeof data!=="object" || !data.workspace)throw Error("Espacio de trabajo incompatible.");
  return {key,data};
}
