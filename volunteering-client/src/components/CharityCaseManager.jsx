import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {
  getCharityCases,
  createCharityCase,
  updateCharityCase,
  deleteCharityCase
} from "../services/api";

function CharityCaseManager() {
  const [cases, setCases] = useState([]);
  const [form, setForm] = useState({ case_name: "", total_amount: "" });
  const [editingId, setEditingId] = useState(null);

  const loadCases = () => {
    getCharityCases().then(res => setCases(res.data));
  };

  useEffect(() => {
    loadCases();

    // Creăm clientul STOMP peste SockJS
    const socket = new SockJS('http://localhost:8080/ws');
    const stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => {
        console.log(str);
      },
      reconnectDelay: 5000,
      onConnect: () => {
        console.log("STOMP connected");

        // Abonare la topic-ul /topic/cases
        stompClient.subscribe('/topic/cases', (message) => {
          const body = JSON.parse(message.body);
          console.log("Mesaj STOMP:", body);

          // Mesajul are forma: { action: "create"|"update"|"delete", data: {...} }
          switch(body.action) {
            case 'create':
              setCases(prev => [...prev, body.data]);
              break;
            case 'update':
              setCases(prev => prev.map(c => c.id === body.data.id ? body.data : c));
              break;
            case 'delete':
              setCases(prev => prev.filter(c => c.id !== body.data.id));
              break;
            default:
              break;
          }
        });
      },
      onStompError: (frame) => {
        console.error('Broker error: ', frame);
      }
    });

    stompClient.activate();

    return () => {
      stompClient.deactivate();
    };
  }, []);

  // restul codului rămâne neschimbat (form, submit, etc)...

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const data = {
      case_name: form.case_name,
      total_amount: parseFloat(form.total_amount)
    };

    if (editingId) {
      updateCharityCase(editingId, data).then(() => {
        loadCases();
        resetForm();
      });
    } else {
      createCharityCase(data).then(() => {
        loadCases();
        resetForm();
      });
    }
  };

  const handleEdit = (charityCase) => {
    setForm({
      case_name: charityCase.case_name,
      total_amount: charityCase.total_amount
    });
    setEditingId(charityCase.id);
  };

  const handleDelete = (id) => {
    deleteCharityCase(id).then(() => loadCases());
  };

  const resetForm = () => {
    setForm({ case_name: "", total_amount: "" });
    setEditingId(null);
  };

  return (
    <div>
      <h2>Cazuri Caritabile</h2>

      <form onSubmit={handleSubmit} style={{ marginBottom: "20px" }}>
        <input
          type="text"
          name="case_name"
          placeholder="Nume caz"
          value={form.case_name}
          onChange={handleChange}
          required
        />
        <input
          type="number"
          name="total_amount"
          placeholder="Suma totală"
          value={form.total_amount}
          onChange={handleChange}
          required
        />
        <button type="submit">{editingId ? "Salvează" : "Adaugă"}</button>
        {editingId && <button type="button" onClick={resetForm}>Anulează</button>}
      </form>

      <table border="1" cellPadding="10" style={{ borderCollapse: "collapse", width: "100%" }}>
        <thead>
          <tr>
            <th>Nume Caz</th>
            <th>Suma Totala</th>
            <th>Acțiuni</th>
          </tr>
        </thead>
        <tbody>
          {cases.map(c => (
            <tr key={c.id}>
              <td>{c.case_name}</td>
              <td>{c.total_amount}</td>
              <td>
                <button onClick={() => handleEdit(c)}>Editează</button>
                <button onClick={() => handleDelete(c.id)}>Șterge</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default CharityCaseManager;
