import test from "node:test";
import assert from "node:assert/strict";
import {webcrypto} from "node:crypto";
import {
  createVault,unlockVault,saveVault,hasVault,getEncryptedBackup,
  importEncryptedBackup,deleteVault,
} from "../lib/vault.mjs";

test("AES-GCM encrypted workspace survives unlocking; never stores tokens in plaintext",async()=>{
  if(!globalThis.crypto)Object.defineProperty(globalThis,"crypto",{value:webcrypto,configurable:true});
  const values=new Map();
  globalThis.localStorage={
    getItem:key=>values.has(key)?values.get(key):null,
    setItem:(key,value)=>values.set(key,value),
    removeItem:key=>values.delete(key),
  };
  try{
    deleteVault();
    const key=await createVault("test-passphrase-long-enough",{
      workspace:{environments:[{name:"Local",variables:{apiToken:"PRIVATE_TOKEN_VALUE"}}]},
      history:[],draft:{url:"https://api.example.com"},
    });
    assert.equal(hasVault(),true);
    const encrypted=getEncryptedBackup();
    assert(!encrypted.includes("PRIVATE_TOKEN_VALUE"));
    await assert.rejects(unlockVault("not-the-password"),/Contraseña incorrecta/);
    const opened=await unlockVault("test-passphrase-long-enough");
    assert.equal(opened.data.workspace.environments[0].variables.apiToken,"PRIVATE_TOKEN_VALUE");
    await saveVault(key,{workspace:{requests:[{name:"Guardada"}]},draft:{url:"https://example.com"},history:[]});
    const loaded=await unlockVault("test-passphrase-long-enough");
    assert.equal(loaded.data.workspace.requests[0].name,"Guardada");
    const backup=getEncryptedBackup();
    deleteVault();
    assert.equal(hasVault(),false);
    importEncryptedBackup(backup);
    assert.equal(hasVault(),true);
    assert.equal((await unlockVault("test-passphrase-long-enough")).data.workspace.requests[0].name,"Guardada");
  }finally{
    deleteVault();
    delete globalThis.localStorage;
  }
});
