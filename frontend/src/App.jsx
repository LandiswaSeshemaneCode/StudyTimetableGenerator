import { useEffect,useState } from "react";
import { createModule,getModules } from "./api/modules";
import "./App.css";

export default function App(){
  const [modules, setModules] = useState([]);
  const [moduleName,setModuleName] = useState("");
  const [loading,setLoading] = useState(true);
  const [saving,setSaving] = useState(false);
  const [loadError, setLoadError] = useState("");
  const [formError,setFormError] = useState("");

  useEffect(()=>{
    let active = true;

    async function loadModules(){
      try{
        const data = await getModules();

        if(active){
          setModules(data);
        }
      }catch(error){
        if(active){
          setLoadError(error.message);
        }
      } finally{
        if(active){
          setLoading(false);
        }
      }
    }

    loadModules();

    return ()=>{
      active = false;
    };

  }, []);

  async function handleAddModule(event){
    event.preventDefault();

    const name = moduleName.trim();

    if(!name){
      setFormError("Enter a module name.");
      return;
    }

    setSaving(true);
    setFormError("");

    try{
      const newModule = await createModule(name);

      setModules((currentModules) => [...currentModules, newModule]);
      setModuleName("");
    }catch(error){
      setFormError(error.message);
    }finally{
      setSaving(false);
    }
  }

  return(
    <main className="app">
      <header className="page-header">
        <p className="eyebrow">Study Timetable</p>
        <h1>My modules</h1>
        <p>Add your modules to start organising your study plan.</p>
      </header>

      <section className="panel" aria-labelledby="add-module-heading">
        <h2 id="add-module-heading">Add a module</h2>

        <form onSubmit={handleAddModule}>
          <label htmlFor="moduleName">Module name</label>

          <div className="form-row">
            <input
              id="moduleName"
              type="text"
              placeholder="For example, Linear Algebra"
              value={moduleName}
              onChange={(event) => setModuleName(event.target.value)}
              maxLength={150}
              required
              disabled={saving}
              />
              <button type="submit" disabled={saving || loading || !!loadError}>
                {saving ? "Adding..." : "Add module"}
              </button>
          </div>
          {formError && (
            <p className="error" role="alert">
              {formError}
            </p>
          )}
        </form>
      </section>
      <section aria-labelledby="module-list-heading" aria-busy={loading}>
        <h2 id="module-list-heading">Your modules</h2>

        {loading ? (
          <p role="status">Loading modules...</p>
        ) : loadError ? (
          <p className="error" role="alert">
            {loadError} Check that your Java backend is running on port 7000,
            then refresh this page.
          </p>
          ) : modules.length === 0 ? (
            <div className="panel empty-state">
              <p>No modules yet. Add your first modules above.</p>
            </div>
            ) :(
              <div className="module-grid">
                {modules.map((module) =>(
                  <article className="module-card" key={module.id}>
                    <span className="module-label">Module</span>
                    <h3>{module.moduleName}</h3>
                  </article>
                ))}
              </div>
            )}
      </section>
    </main>
  );
}