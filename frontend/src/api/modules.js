const API_URL = "http://localhost:7000";

async function request(path, options = {}){
const response = await fetch(`${API_URL}${path}`,options);
const data = await response.json();

if(!response.ok){
    throw new Error(data.error || "Something went wrong . Please try again.");

}
return data;
}

export function getModules(){
    return request("/modules");
}

export function createModule(moduleName){
    return request("/modules",{
        method: "POST",
        headers:{
            "Content-Type": "application/json",
        },
        body: JSON.stringify({moduleName}),
    });
}