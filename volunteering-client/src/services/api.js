import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/charity-cases';

export const getCharityCases  = () => axios.get(API_BASE);
export const createCharityCase = (data) => axios.post(API_BASE, data);
export const updateCharityCase = (id, data) => axios.put(`${API_BASE}/${id}`, data);
export const deleteCharityCase = (id) => axios.delete(`${API_BASE}/${id}`);