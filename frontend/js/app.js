const API_URL = 'http://localhost:8080';
const token = localStorage.getItem('token');

let allTodos = []; // 所有数据
let currentFilter = 'all'; //当前筛选

const btnPost = document.querySelector('.btn--post');
const btnDeleteCompleted = document.querySelector('.btn--deleteCompleted');
const logoutBtn = document.getElementById('logout-btn');
const btnAI = document.querySelector('.btn--ai');

const todoList = document.getElementById('todo-list');
const username = localStorage.getItem('username');

///////////////////////////////////////////////////////////////
//todos的函数
//渲染函数

function renderTodos() {
  const list = document.getElementById('todo-list');
  list.innerHTML = '';

  const todos = sortTodos(getFilteredTodos());

  todos.forEach(todo => {
    const li = document.createElement('li');
    const now = new Date();
    const isOverdue =
      todo.dueDate && !todo.completed && new Date(todo.dueDate) < now;

    if (isOverdue) {
      li.classList.add('overDue');
    }

    const timeAgo = formatTimeAgo(todo.updatedAt);
    const ddl = todo.dueDate
      ? new Date(todo.dueDate).toLocaleDateString()
      : null;

    li.innerHTML = `
      <label class="todo-item">
        <input 
          type="checkbox" 
          class="todo-checkbox"
          data-id="${todo.id}"
          ${todo.completed ? 'checked' : ''}
        />

        <div class="todo-content">
          <div class="todo-title ${todo.completed ? 'completed' : ''}">
            ${todo.title}
          </div>

          ${ddl ? `<div class="todo-ddl">DDL: ${ddl}</div>` : ''}
        </div>
      </label>

      <div class="todo-meta">
        ${timeAgo}
      </div>

      <div>
        <button class="btn--delete" data-id="${todo.id}">
          删除
        </button>
      </div>
    `;

    list.appendChild(li);
  });
}

//规范时间
function formatTimeAgo(timeStr) {
  const now = new Date();
  const time = new Date(timeStr);
  const diff = Math.floor((now - time) / 1000);

  if (diff < 60) return '刚刚';
  if (diff < 3600) return `${Math.floor(diff / 60)}分钟前`;
  if (diff < 86400) return `${Math.floor(diff / 3600)}小时前`;
  if (diff < 604800) return `${Math.floor(diff / 86400)}天前`;

  return time.toLocaleDateString();
}

//更新筛选UI
function updateFilterUI() {
  document.querySelectorAll('.filters button').forEach(btn => {
    btn.classList.remove('active');
  });

  document.getElementById(`filter-${currentFilter}`).classList.add('active');
}

//筛选任务
function getFilteredTodos() {
  if (currentFilter === 'active') {
    return allTodos.filter(todo => !todo.completed);
  }
  if (currentFilter === 'completed') {
    return allTodos.filter(todo => todo.completed);
  }
  return allTodos;
}

// 获取所有 todo
async function fetchTodos() {
  try {
    const response = await fetch(`${API_URL}/todos`, {
      method: 'GET',
      headers: {
        Authorization: 'Bearer ' + token,
      },
    });

    if (!response.ok) {
      throw new Error('获取失败');
    }

    const data = await response.json();
    allTodos = data;
    // console.log(data);
    renderTodos();
  } catch (error) {
    console.error(error);
  }
}

//增加任务
async function addTodoSingle() {
  const input = document.getElementById('todo-input');
  const title = input.value;

  const ddl_input = document.getElementById('todo-ddl-input');
  const ddl = ddl_input.value ? ddl_input.value + 'T00:00:00' : null;
  // console.log(ddl);

  if (!title) return;

  await fetch(`${API_URL}/todos`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + token,
    },
    body: JSON.stringify({
      title: title,
      completed: false,
      dueDate: ddl,
    }),
  });

  input.value = '';
  fetchTodos(); // 重新加载列表
}

//批量增加任务
async function addTodosBatch() {
  const input = document.getElementById('ai-input');
  const titles = input.value;

  const tasks = await parseWithAI(titles);
  // console.log(tasks);

  await fetch(`${API_URL}/todos/batch`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + token,
    },
    body: JSON.stringify(
      tasks.map(t => ({
        title: t.title,
        completed: false,
        dueDate: t.dueDate || null,
      })),
    ),
  });

  input.value = '';
  fetchTodos(); // 刷新列表
}

//AI交互
async function parseWithAI(text) {
  try {
    const res = await fetch(`${API_URL}/ai/parse`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer ' + token,
      },
      body: JSON.stringify({ text }),
    });

    if (!res.ok) {
      throw new Error('HTTP error ' + res.status);
    }

    const datas = await res.json();

    // console.log('AI返回:', datas);

    return datas;
  } catch (err) {
    console.error('请求失败:', err);
    throw err;
  }
}

//删除任务
async function deleteTodo(id) {
  await fetch(`${API_URL}/todos/${id}`, {
    method: 'DELETE',
    headers: {
      Authorization: 'Bearer ' + token,
    },
  });

  fetchTodos(); // 删除后刷新
}

//删除已完成任务
async function deleteCompleteTodo() {
  const response = await fetch(`${API_URL}/todos/completed`, {
    method: 'DELETE',
    headers: {
      Authorization: 'Bearer ' + token,
    },
  });
  const data = await response.json();
  showToast(`delete ${data.deleted} completed todos`);
  fetchTodos(); // 删除后刷新
}

//更新任务
async function toggleTodo(id, completed) {
  await fetch(`${API_URL}/todos/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      Authorization: 'Bearer ' + token,
    },
    body: JSON.stringify({
      completed: !completed,
    }),
  });

  fetchTodos();
}

//提示函数
function showToast(message) {
  const toast = document.getElementById('toast');

  toast.innerText = message;
  toast.style.display = 'block';

  setTimeout(() => {
    toast.style.display = 'none';
    toast.innerText = '';
  }, 2000);
}

//根据ddl时间进行排序
function sortTodos(todos) {
  return todos.sort((a, b) => {
    const aDDL = a.dueDate ? new Date(a.dueDate) : null;
    const bDDL = b.dueDate ? new Date(b.dueDate) : null;

    if (!aDDL && !bDDL) return 0;
    if (!aDDL) return 1;
    if (!bDDL) return -1;

    return aDDL - bDDL;
  });
}

///////////////////////////////////////////////////////////////

//判断是否登录
if (!token) {
  window.location.href = 'login.html';
}

//显示用户名
if (username) {
  document.getElementById('username').innerText = username;
}

// 页面加载时渲染当前的任务
fetchTodos();
updateFilterUI();

//按添加按钮或回车键增加任务
btnPost.addEventListener('click', addTodoSingle);
document.addEventListener('keydown', function (e) {
  if (e.key === 'Enter') addTodoSingle();
});

//删除按钮
todoList.addEventListener('click', function (e) {
  const press = e.target;

  if (press.classList.contains('btn--delete')) {
    e.preventDefault();
    deleteTodo(Number(press.dataset.id));
  }
});

//更新任务
todoList.addEventListener('change', function (e) {
  const target = e.target;

  if (target.classList.contains('todo-checkbox')) {
    const id = Number(target.dataset.id);
    const completed = target.checked;

    toggleTodo(id, !completed);
  }
});

//删除所有已完成任务
btnDeleteCompleted.addEventListener('click', deleteCompleteTodo);

//登出按钮
logoutBtn.addEventListener('click', () => {
  localStorage.removeItem('token');
  localStorage.removeItem('username');
  window.location.href = 'index.html';
});

//筛选按钮
document.getElementById('filter-all').onclick = () => {
  currentFilter = 'all';
  renderTodos();
  updateFilterUI();
};

document.getElementById('filter-active').onclick = () => {
  currentFilter = 'active';
  renderTodos();
  updateFilterUI();
};

document.getElementById('filter-completed').onclick = () => {
  currentFilter = 'completed';
  renderTodos();
  updateFilterUI();
};

//AI解析按钮
btnAI.addEventListener('click', addTodosBatch);
